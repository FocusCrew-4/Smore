package com.smore.bidcompetition.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.bidcompetition.application.dto.BidCreateCommand;
import com.smore.bidcompetition.application.dto.CompetitionCommand;
import com.smore.bidcompetition.application.dto.OrderCompletedCommand;
import com.smore.bidcompetition.application.dto.OrderFailedCommand;
import com.smore.bidcompetition.application.dto.RefundSucceededCommand;
import com.smore.bidcompetition.application.dto.ServiceResult;
import com.smore.bidcompetition.application.exception.BidConflictException;
import com.smore.bidcompetition.application.exception.WinnerConflictException;
import com.smore.bidcompetition.application.repository.BidCompetitionRepository;
import com.smore.bidcompetition.application.repository.BidInventoryLogRepository;
import com.smore.bidcompetition.application.repository.OutboxRepository;
import com.smore.bidcompetition.application.repository.WinnerRepository;
import com.smore.bidcompetition.domain.model.BidCompetition;
import com.smore.bidcompetition.domain.model.BidInventoryLog;
import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.domain.model.Winner;
import com.smore.bidcompetition.domain.status.AggregateType;
import com.smore.bidcompetition.domain.status.EventType;
import com.smore.bidcompetition.domain.status.InventoryChangeType;
import com.smore.bidcompetition.domain.status.WinnerStatus;
import com.smore.bidcompetition.infrastructure.error.BidErrorCode;
import com.smore.bidcompetition.infrastructure.persistence.event.outbound.BidEvent;
import com.smore.bidcompetition.infrastructure.persistence.event.outbound.BidProductInventoryAdjustedEvent;
import com.smore.bidcompetition.infrastructure.persistence.event.outbound.InventoryConfirmationTimeOutEvent;
import com.smore.bidcompetition.infrastructure.persistence.event.outbound.WinnerCreatedEvent;
import com.smore.bidcompetition.infrastructure.redis.StockRedisService;
import com.smore.bidcompetition.presentation.dto.BidResponse;
import io.lettuce.core.RedisCommandTimeoutException;
import io.micrometer.tracing.Tracer;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "BidCompetitionService")
@Service
@RequiredArgsConstructor
public class BidCompetitionService {

    private final RedisTemplate<Object, Object> redisTemplate;
    @Value("${app.allocation.valid-duration}")
    private long validDurationSeconds;

    @Value("${app.allocation.propagation-buffer-time}")
    private long bufferTimeSeconds;

    private final BidCompetitionRepository bidCompetitionRepository;
    private final WinnerRepository winnerRepository;
    private final OutboxRepository outboxRepository;
    private final BidInventoryLogRepository bidInventoryLogRepository;
    private final StockRedisService stockRedisService;
    private final Tracer tracer;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @Transactional
    public void createBid(BidCreateCommand command) {

        BidCompetition bid = bidCompetitionRepository.findByIdempotencyKey(
            command.getIdempotencyKey());

        if (bid != null) {
            log.info("이미 처리된 작업입니다.");
            return;
        }

        BidCompetition newBid = BidCompetition.create(
            command.getProductId(),
            command.getCategoryId(),
            command.getSellerId(),
            command.getProductPrice(),
            command.getStock(),
            command.getStock(),
            command.getIdempotencyKey(),
            command.getStartAt(),
            command.getEndAt()
        );

        BidCompetition saved = bidCompetitionRepository.save(newBid);

        try {
            long setResult = stockRedisService.setStock(saved.getId(), saved.getTotalQuantity());

            if (setResult == -1L) {
                log.error("재고 초기화 실패: bidId={}, stock={}", saved.getId(), saved.getTotalQuantity());
            } else if (setResult == 0L) {
                log.info("이미 재고 키가 존재합니다. bidId={}", saved.getId());
            } else {
                log.info("재고 초기화 완료: bidId={}, stock={}", saved.getId(), setResult);
            }
        } catch (Exception e) {
            log.error("재고 초기화 중 예외 발생. bidId={}", saved.getId(), e);
        }
    }

    @Transactional
    public BidResponse competition(CompetitionCommand command) {

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expireAt = now.plusSeconds(validDurationSeconds);
        UUID allocationKey = UUID.nameUUIDFromBytes(
            ("ALLOC:" + command.getBidId() + ":" + command.getIdempotencyKey()).getBytes(StandardCharsets.UTF_8)
        );

        long keyTtl = validDurationSeconds + bufferTimeSeconds + 120L;

        try {
            long reserveResult = stockRedisService.reserve(
                command.getBidId(),
                allocationKey.toString(),
                command.getIdempotencyKey().toString(),
                command.getUserId().toString(),
                command.getQuantity(),
                keyTtl
            );

            // 처리중이거나 이미 처리된 작업
            if (reserveResult == -2L) {

                Winner winner = winnerRepository.findByIdempotencyKey(command.getBidId(), command.getIdempotencyKey());

                if (winner != null) {
                    return BidResponse.success(
                        winner.getBidId(),
                        winner.getQuantity(),
                        winner.getAllocationKey(),
                        winner.getExpireAt()
                    );
                }

                return BidResponse.processing(
                    command.getBidId(),
                    command.getQuantity(),
                    allocationKey,
                    "처리중/중복 요청"
                );
            }

            if (reserveResult != 1L) {
                return BidResponse.fail(
                    command.getBidId(),
                    command.getQuantity(),
                    "재고 부족 또는 확보 실패"
                );
            }
        } catch (RedisCommandTimeoutException e) {
            return BidResponse.processing(
                command.getBidId(),
                command.getQuantity(),
                allocationKey,
                "일시적 네트워크 오류. 재시도해주세요."
            );
        } catch (Exception e) {
            log.error("reserve 단계 예외", e);

            stockRedisService.rollback(
                command.getBidId(),
                allocationKey.toString(),
                command.getIdempotencyKey().toString(),
                command.getQuantity()
            );

            return BidResponse.fail(
                command.getBidId(),
                command.getQuantity(),
                "예기치 못한 예외 발생"
            );
        }

        try {
            Winner winner = winnerRepository.findByIdempotencyKey(command.getBidId(), command.getIdempotencyKey());

            if (winner != null) {
                log.info("이미 처리된 작업입니다. userId : {}, bidId : {} idempotencyKey : {}",
                    command.getUserId(), command.getBidId(), command.getIdempotencyKey());

                stockRedisService.rollback(
                    command.getBidId(),
                    allocationKey.toString(),
                    command.getIdempotencyKey().toString(),
                    command.getQuantity()
                );

                return BidResponse.success(
                    winner.getBidId(),
                    winner.getQuantity(),
                    winner.getAllocationKey(),
                    winner.getExpireAt()
                );
            }

            // 비관락
            BidCompetition bid = bidCompetitionRepository.findByIdForUpdate(command.getBidId());

            // 경쟁 상태 점검
            if (bid.isNotAvailable() || bid.isEnd(now)) {
                log.info("판매 경쟁이 종료되었습니다.");

                stockRedisService.rollback(
                    command.getBidId(),
                    allocationKey.toString(),
                    command.getIdempotencyKey().toString(),
                    command.getQuantity()
                );

                return BidResponse.fail(
                    command.getBidId(),
                    command.getQuantity(),
                    "판매 경쟁이 종료되어 주문을 받을 수 없습니다."
                );
            }

            // 재고 확인 및 확보
            int updated = bidCompetitionRepository.decreaseStock(
                command.getBidId(),
                command.getQuantity(),
                now
            );

            // 재고 확보 실패
            if (updated == 0) {
                log.info("재고 확보에 실패했습니다 userId : {}, bidId : {}, quantity : {}",
                    command.getUserId(), command.getBidId(), command.getQuantity());

                stockRedisService.rollback(
                    command.getBidId(),
                    allocationKey.toString(),
                    command.getIdempotencyKey().toString(),
                    command.getQuantity()
                );

                return BidResponse.fail(
                    command.getBidId(),
                    command.getQuantity(),
                    "재고 확보에 실패했습니다."
                );
            }

            Winner newWinner = Winner.create(
                command.getUserId(),
                bid.getId(),
                bid.getProductId(),
                command.getQuantity(),
                allocationKey,
                command.getIdempotencyKey(),
                now,
                expireAt
            );

            // Winner 등록
            Winner savedWinner = winnerRepository.save(newWinner);

            WinnerCreatedEvent event = WinnerCreatedEvent.of(
                command.getUserId(),
                bid.getProductId(),
                bid.getProductPrice().intValue(),
                command.getQuantity(),
                bid.getCategoryId(),
                bid.getSellerId(),
                allocationKey,
                expireAt,
                command.getStreet(),
                command.getCity(),
                command.getZipcode()
            );

            String idempotencyKey = InventoryChangeType.RESERVE.idempotencyKey(
                String.valueOf(allocationKey)
            );

            Integer delta = command.getQuantity();

            Integer stockBefore = bid.getStock();
            Integer stockAfter = stockBefore - delta;

            BidInventoryLog log = BidInventoryLog.create(
                bid.getId(),
                savedWinner.getId(),
                InventoryChangeType.RESERVE,
                stockBefore,
                stockAfter,
                delta,
                idempotencyKey,
                now
            );

            bidInventoryLogRepository.saveAndFlush(log);

            Outbox outbox = Outbox.create(
                AggregateType.BID,
                bid.getId(),
                EventType.BID_WINNER_SELECTED,
                UUID.randomUUID(),
                makePayload(event)
            );

            if (tracer.currentSpan() != null) {
                outbox.attachTracing(
                    tracer.currentSpan().context().traceId(),
                    tracer.currentSpan().context().spanId()
                );
            }

            outboxRepository.save(outbox);

            return BidResponse.success(
                savedWinner.getBidId(),
                savedWinner.getQuantity(),
                savedWinner.getAllocationKey(),
                savedWinner.getExpireAt()
            );
        } catch (Exception e) {

            stockRedisService.rollback(
                command.getBidId(),
                allocationKey.toString(),
                command.getIdempotencyKey().toString(),
                command.getQuantity()
            );

            throw e;
        }

    }

    @Transactional
    public ServiceResult orderCompleted(OrderCompletedCommand command) {

        Winner winner = winnerRepository.findByAllocationKey(command.getAllocationKey());

        if (winner.isPaid()) {
            log.info("이미 처리된 작업입니다. allocationKey : {}", command.getAllocationKey());
            return ServiceResult.SUCCESS;
        }

        if (winner.isExpired()) {
            log.warn("이미 만료된 Winner에 대한 결제 완료 이벤트 입니다. allocationKey : {}",
                command.getAllocationKey());

            InventoryConfirmationTimeOutEvent event = InventoryConfirmationTimeOutEvent.of(
                command.getOrderId(),
                command.getUserId(),
                winner.getQuantity(),
                "재고 확보 실패",
                winner.getAllocationKey()
            );

            Outbox outbox = Outbox.create(
                AggregateType.BID,
                winner.getBidId(),
                EventType.BID_INVENTORY_CONFIRM_TIMEOUT,
                UUID.randomUUID(),
                makePayload(event)
            );

            if (tracer.currentSpan() != null) {
                outbox.attachTracing(
                    tracer.currentSpan().context().traceId(),
                    tracer.currentSpan().context().spanId()
                );
            }

            outboxRepository.save(outbox);

            return ServiceResult.FAIL;

        }

        LocalDateTime validAt = winner.getExpireAt().plusSeconds(bufferTimeSeconds);
        LocalDateTime now = LocalDateTime.now(clock);

        if (now.isAfter(validAt)) {
            log.error("결제 유효 시간을 초과하셨습니다.");

            InventoryConfirmationTimeOutEvent event = InventoryConfirmationTimeOutEvent.of(
                command.getOrderId(),
                command.getUserId(),
                winner.getQuantity(),
                "재고 확보 실패",
                winner.getAllocationKey()
            );

            Outbox outbox = Outbox.create(
                AggregateType.BID,
                winner.getBidId(),
                EventType.BID_INVENTORY_CONFIRM_TIMEOUT,
                UUID.randomUUID(),
                makePayload(event)
            );

            if (tracer.currentSpan() != null) {
                outbox.attachTracing(
                    tracer.currentSpan().context().traceId(),
                    tracer.currentSpan().context().spanId()
                );
            }

            outboxRepository.save(outbox);

            return ServiceResult.FAIL;
        }

        int updated = winnerRepository.winnerPaid(command.getAllocationKey(), command.getOrderId(), winner.getVersion());

        if (updated == 0) {
            log.error("동시성 충돌로 인해 작업을 처리하지 못했습니다. allocationKey : {}", command.getAllocationKey());
            throw new WinnerConflictException(BidErrorCode.WINNER_CONFLICT);
        }

        stockRedisService.confirmCleanup(
            winner.getBidId(),
            winner.getAllocationKey().toString(),
            winner.getIdempotencyKey().toString()
        );

        return ServiceResult.SUCCESS;
    }

    @Transactional
    public void orderFailed(OrderFailedCommand command) {

        // TODO: allocationKey로 Winner 미존재 시 재시도 루프 방지 처리 추가 필요
        Winner winner = winnerRepository.findByAllocationKey(command.getAllocationKey());

        if (winner.isCompleted()) {
            log.info("이미 처리된 작업입니다. allocationKey : {}", command.getAllocationKey());
            return;
        }

        BidCompetition bid = bidCompetitionRepository.findByIdForUpdate(winner.getBidId());

        int updated = winnerRepository.markCancelled(
            winner.getBidId(),
            command.getAllocationKey(),
            WinnerStatus.CANCELABLE_STATUSES,
            winner.getVersion()
        );

        if (updated == 0) {
            log.error("동시성 충돌로 인해 작업을 처리하지 못했습니다. allocationKey : {}", command.getAllocationKey());
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
                    winner.getBidId(), command.getAllocationKey());
                return;
            }

            log.error("재고 로그 저장 실패 (중복 아님). bidId={}, allocationKey={}, cause={}",
                winner.getBidId(), command.getAllocationKey(), message, e);
            throw e;
        }


        updated = bidCompetitionRepository.increaseStock(winner.getBidId(), winner.getQuantity());

        if (updated == 0) {
            log.error("예기치 못한 예외로 인해 처리하지 못했습니다. allocationKey : {}", command.getAllocationKey());
            throw new WinnerConflictException(BidErrorCode.WINNER_CONFLICT);
        }

        stockRedisService.rollback(
            winner.getBidId(),
            winner.getAllocationKey().toString(),
            winner.getIdempotencyKey().toString(),
            winner.getQuantity()
        );
    }

    @Transactional
    public void refundSuccess(RefundSucceededCommand command) {

        Winner winner = winnerRepository.findByAllocationKey(command.getAllocationKey());

        if (winner.isNotPaid()) {
            log.info("이미 처리된 작업입니다. allocationKey : {}", command.getAllocationKey());
            return;
        }

        BidCompetition bid = bidCompetitionRepository.findByIdForUpdate(winner.getBidId());

        if (bid.isEnd()) {
            BidProductInventoryAdjustedEvent event = BidProductInventoryAdjustedEvent.of(
                bid.getId(),
                bid.getProductId(),
                command.getQuantity(),
                command.getRefundId()
            );

            Outbox outbox = Outbox.create(
                AggregateType.PRODUCT,
                bid.getProductId(),
                EventType.PRODUCT_INVENTORY_ADJUSTED,
                UUID.randomUUID(),
                makePayload(event)
            );

            if (tracer.currentSpan() != null) {
                outbox.attachTracing(
                    tracer.currentSpan().context().traceId(),
                    tracer.currentSpan().context().spanId()
                );
            }

            outboxRepository.save(outbox);
            return;
        }

        Integer delta = command.getQuantity();
        if (delta == null || delta <= 0 || delta > winner.getQuantity()) {
            log.error("delta 값이 잘못되었습니다.");
            throw new IllegalArgumentException("delta 값이 잘못되었습니다.");
        }

        Integer stockBefore = bid.getStock();
        Integer stockAfter = stockBefore + delta;

        // 로그 기록 필요
        BidInventoryLog inventoryLog = BidInventoryLog.create(
            winner.getBidId(),
            winner.getId(),
            InventoryChangeType.REFUND,
            stockBefore,
            stockAfter,
            delta,
            InventoryChangeType.REFUND.idempotencyKey(String.valueOf(command.getRefundId())),
            LocalDateTime.now(clock)
        );

        try {
            bidInventoryLogRepository.saveAndFlush(inventoryLog);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause() != null
                ? e.getMostSpecificCause().getMessage()
                : e.getMessage();

            if (message != null && message.contains(("uk_bid_idempotency_key"))) {
                log.info("이미 처리된 환불 이벤트입니다. bidId={}, refundId={}",
                    winner.getBidId(), command.getRefundId());
                return;
            }

            log.error("재고 로그 저장 실패 (중복 아님). bidId={}, refundId={}, cause={}",
                winner.getBidId(), command.getRefundId(), message, e);
            throw e;
        }

        // 재고 복구
        int updated = bidCompetitionRepository.increaseStock(
            winner.getBidId(),
            command.getQuantity()
        );

        if (updated == 0) {
            log.error("예기치 못한 예외로 인해 처리하지 못했습니다. allocationKey : {}", command.getAllocationKey());
            throw new BidConflictException(BidErrorCode.BID_CONFLICT);
        }

        // 전체 환불인 경우
        if (command.isRefunded()) {
            updated = winnerRepository.markCancelled(
                winner.getBidId(),
                command.getAllocationKey(),
                WinnerStatus.CANCELABLE_STATUSES,
                winner.getVersion()
            );

            // 낙관락이므로 동일한 상태에 대해서 변경을 시도했을 수 있음
            if (updated == 0) {
                log.error("동시성 충돌로 인해 작업을 처리하지 못했습니다. allocationKey : {}", command.getAllocationKey());
                throw new WinnerConflictException(BidErrorCode.WINNER_CONFLICT);
            }
        }


        long restored = stockRedisService.refundRestore(
            winner.getBidId(),
            command.getRefundId(),
            delta
        );
        if (restored == 0) {
            log.info("이미 처리된 환불 복구입니다. bidId={}, refundId={}", winner.getBidId(),
                command.getRefundId());
        } else if (restored < 0) {
            log.error("환불 복구 실패(redis) bidId={}, refundId={}, result={}", winner.getBidId(),
                command.getRefundId(), restored);
        }
    }

    // TODO: 나중에 클래스로 분리할 예정
    private String makePayload(BidEvent event)  {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("이벤트 Payload JSON 변환 실패");
            throw new RuntimeException(e);
        }
    }
}
