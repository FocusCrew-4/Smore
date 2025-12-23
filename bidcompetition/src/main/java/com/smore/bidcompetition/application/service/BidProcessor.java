package com.smore.bidcompetition.application.service;

import com.smore.bidcompetition.application.exception.BidConflictException;
import com.smore.bidcompetition.application.exception.WinnerConflictException;
import com.smore.bidcompetition.application.repository.BidCompetitionRepository;
import com.smore.bidcompetition.application.repository.BidInventoryLogRepository;
import com.smore.bidcompetition.application.repository.WinnerRepository;
import com.smore.bidcompetition.domain.model.BidCompetition;
import com.smore.bidcompetition.domain.model.BidInventoryLog;
import com.smore.bidcompetition.domain.model.Winner;
import com.smore.bidcompetition.domain.status.InventoryChangeType;
import com.smore.bidcompetition.infrastructure.error.BidErrorCode;
import com.smore.bidcompetition.infrastructure.redis.StockRedisService;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "bidProcessor")
@Service
@RequiredArgsConstructor
public class BidProcessor {

    @Value("${app.allocation.close-grace-seconds}")
    private long closeGraceSeconds;

    @Value("${app.allocation.propagation-buffer-time}")
    private long bufferTime;

    private final BidCompetitionRepository bidCompetitionRepository;
    private final BidInventoryLogRepository bidInventoryLogRepository;
    private final StockRedisService stockRedisService;
    private final WinnerRepository winnerRepository;
    private final BidEndFinalizer bidEndFinalizer;
    private final Clock clock;

    @Async("bidTaskExecutor")
    @Transactional
    public void active() {
        try {
            LocalDateTime now = LocalDateTime.now(clock);

            List<UUID> scheduledToActivateIds = bidCompetitionRepository
                .findBidsToActivate(now);

            if (scheduledToActivateIds.isEmpty()) {
                log.info("[ACTIVE] 수행할 작업이 없습니다.");
                return;
            }

            int activated = bidCompetitionRepository.bulkActivateByStartAt(scheduledToActivateIds, now);
            log.info("{}개 중 {}개가 활성화 되었습니다", scheduledToActivateIds.size(), activated);
        } catch (Exception e) {
            log.error("active failed", e);
        }
    }

    @Async("bidTaskExecutor")
    @Transactional
    public void close() {
        try {
            LocalDateTime now = LocalDateTime.now(clock);

            List<UUID> activeToCloseIds = bidCompetitionRepository
                .findBidsToClose(now);

            if (activeToCloseIds.isEmpty()) {
                log.info("[CLOSE] 수행할 작업이 없습니다.");
                return;
            }

            int closed = bidCompetitionRepository.bulkCloseByEndAt(activeToCloseIds, now);
            log.info("{}개 중 {}개가 종료 되었습니다", activeToCloseIds.size(), closed);
        } catch (Exception e) {
            log.error("close failed", e);
        }
    }

    @Async("bidTaskExecutor")
    @Transactional
    public void end() {
        try {
            LocalDateTime now = LocalDateTime.now(clock);

            List<BidCompetition> bids = bidCompetitionRepository.findBidListToEnd(
                now,
                closeGraceSeconds
            );

            if (bids.isEmpty()) {
                log.info("[END] 수행할 작업이 없습니다.");
                return;
            }

            for (BidCompetition bid : bids) {
                try {
                    bidEndFinalizer.finalizeBid(bid.getId(), now);
                } catch (Exception e) {
                    log.error("END 상태로 전환하지 못했습니다. bidId : {}", bid.getId());
                }

            }

        } catch (Exception e) {
            log.error("end failed", e);
        }
    }

    public Page<UUID> getExpiredWinnerIds(PageRequest pageRequest) {
        LocalDateTime now = LocalDateTime.now(clock);
        return winnerRepository.findExpiredWinners(now, bufferTime, pageRequest);
    }

    @Async("winnerTaskExecutor")
    @Transactional
    public void recoveryStock(UUID winnerId) {

        Winner winner = winnerRepository.findById(winnerId);

        BidCompetition bid = bidCompetitionRepository.findByIdForUpdate(winner.getBidId());

        int updated = winnerRepository.markExpired(winnerId, winner.getVersion());

        if (updated == 0) {
            log.error("winner 정보 수정에 실패했습니다. winnerId : {}", winner.getId());
            throw new WinnerConflictException(BidErrorCode.WINNER_CONFLICT);
        }

        Integer delta = winner.getQuantity();
        Integer stockBefore = bid.getStock();
        Integer stockAfter = stockBefore + delta;

        // 로그 기록 필요
        BidInventoryLog inventoryLog = BidInventoryLog.create(
            winner.getBidId(),
            winner.getId(),
            InventoryChangeType.EXPIRED,
            stockBefore,
            stockAfter,
            delta,
            InventoryChangeType.EXPIRED.idempotencyKey(String.valueOf(winner.getAllocationKey())),
            LocalDateTime.now(clock)
        );

        try {
            bidInventoryLogRepository.saveAndFlush(inventoryLog);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause() != null
                ? e.getMostSpecificCause().getMessage()
                : e.getMessage();

            if (message != null && message.contains(("uk_bid_idempotency_key"))) {
                log.info("이미 처리된 실패 이벤트입니다. bidId={}, allocationKey={}",
                    winner.getBidId(), winner.getAllocationKey());
                return;
            }

            log.error("재고 로그 저장 실패 (중복 아님). bidId={}, allocationKey={}, cause={}",
                winner.getBidId(), winner.getAllocationKey(), message, e);
            throw e;
        }

        updated = bidCompetitionRepository.increaseStock(winner.getBidId(), winner.getQuantity());

        if (updated == 0) {
            log.error("Bid 재고 복구에 실패했습니다. winnerId : {} bidId : {}",
                winner.getId(), winner.getBidId());
            throw new BidConflictException(BidErrorCode.BID_CONFLICT);
        }

        stockRedisService.rollback(
            winner.getBidId(),
            winner.getAllocationKey().toString(),
            winner.getIdempotencyKey().toString(),
            winner.getQuantity()
        );
    }

}
