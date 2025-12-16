package com.smore.bidcompetition.application.service;

import com.smore.bidcompetition.application.repository.BidCompetitionRepository;
import com.smore.bidcompetition.domain.model.BidCompetition;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j(topic = "bidProcessor")
@Service
@RequiredArgsConstructor
public class BidProcessor {

    @Value("${app.allocation.close-grace-seconds}")
    private long closeGraceSeconds;

    private final BidCompetitionRepository bidCompetitionRepository;
    private final BidEndFinalizer bidEndFinalizer;
    private final Clock clock;

    @Async("bidTaskExecutor")
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

}
