package com.smore.bidcompetition.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.bidcompetition.application.dto.BidCreateCommand;
import com.smore.bidcompetition.application.dto.CompetitionCommand;
import com.smore.bidcompetition.application.dto.OrderCompletedCommand;
import com.smore.bidcompetition.application.dto.ServiceResult;
import com.smore.bidcompetition.application.exception.WinnerConflictException;
import com.smore.bidcompetition.application.repository.BidCompetitionRepository;
import com.smore.bidcompetition.application.repository.OutboxRepository;
import com.smore.bidcompetition.application.repository.WinnerRepository;
import com.smore.bidcompetition.domain.model.BidCompetition;
import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.domain.model.Winner;
import com.smore.bidcompetition.domain.status.AggregateType;
import com.smore.bidcompetition.domain.status.EventType;
import com.smore.bidcompetition.infrastructure.error.BidErrorCode;
import com.smore.bidcompetition.infrastructure.persistence.event.outbound.WinnerCreatedEvent;
import com.smore.bidcompetition.presentation.dto.BidResponse;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "BidCompetitionService")
@Service
@RequiredArgsConstructor
public class BidCompetitionService {

    @Value("${app.allocation.valid-duration}")
    private long validDurationSeconds;

    @Value("${app.allocation.propagation-buffer-time}")
    private long bufferTimeSeconds;

    private final BidCompetitionRepository bidCompetitionRepository;
    private final WinnerRepository winnerRepository;
    private final OutboxRepository outboxRepository;
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
            command.getIdempotencyKey(),
            command.getStartAt(),
            command.getEndAt()
        );

        bidCompetitionRepository.save(newBid);
    }

    @Transactional
    public BidResponse competition(CompetitionCommand command) {

        Winner winner = winnerRepository.findByIdempotencyKey(command.getIdempotencyKey());

        if (winner != null) {
            log.info("이미 처리된 작업입니다. userId : {}, bidId : {} idempotencyKey : {}",
                command.getUserId(), command.getBidId(), command.getIdempotencyKey());
            return BidResponse.success(
                winner.getBidId(),
                winner.getProductId(),
                winner.getQuantity(),
                winner.getAllocationKey(),
                winner.getExpireAt()
            );
        }

        // 비관락
        BidCompetition bid = bidCompetitionRepository.findByIdForUpdate(command.getBidId());

        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expireAt = now.plusSeconds(validDurationSeconds);

        // 경쟁 상태 점검
        if (bid.isNotActive() || bid.isExpired(now)) {
            log.info("판매 경쟁이 종료되었습니다.");
            return BidResponse.fail(
                command.getBidId(),
                bid.getProductId(),
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
            return BidResponse.fail(
                command.getBidId(),
                bid.getProductId(),
                command.getQuantity(),
                "재고 확보에 실패했습니다."
            );
        }


        UUID allocationKey = UUID.randomUUID();
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
            bid.getProductPrice().intValue(), // FIXME: 나중에 수정해야 함
            command.getQuantity(),
            bid.getCategoryId(),
            bid.getSellerId(),
            allocationKey,
            expireAt,
            command.getStreet(),
            command.getCity(),
            command.getZipcode()
        );


        Outbox outbox = Outbox.create(
            AggregateType.BID,
            bid.getId(),
            EventType.BID_WINNER_SELECTED,
            UUID.randomUUID(),
            makePayload(event)
        );

        // Winner가 등록된 후, 등록되었음을 알리는 이벤트 발행
        outboxRepository.save(outbox);

        return BidResponse.success(
            savedWinner.getBidId(),
            savedWinner.getProductId(),
            savedWinner.getQuantity(),
            savedWinner.getAllocationKey(),
            savedWinner.getExpireAt()
        );

    }

    @Transactional
    public ServiceResult orderCompleted(OrderCompletedCommand command) {

        Winner winner = winnerRepository.findByAllocationKey(command.getAllocationKey());

        if (winner.isCompleted()) {
            log.info("이미 처리된 작업입니다. allocationKey : {}", command.getAllocationKey());
            return ServiceResult.SUCCESS;
        }

        if (winner.isExpired()) {
            log.warn("이미 만료된 Winner에 대한 결제 완료 이벤트 입니다. allocationKey : {}",
                command.getAllocationKey());
            return ServiceResult.FAIL;

        }

        LocalDateTime validAt = winner.getExpireAt().plusSeconds(bufferTimeSeconds);
        LocalDateTime now = LocalDateTime.now(clock);

        if (now.isAfter(validAt)) {
            log.error("결제 유효 시간을 초과하셨습니다.");
            return ServiceResult.FAIL;
        }

        int updated = winnerRepository.winnerPaid(command.getAllocationKey(), command.getOrderId(), winner.getVersion());

        if (updated == 0) {
            log.error("동시성 충돌로 인해 작업을 처리하지 못했습니다. allocationKey : {}", command.getAllocationKey());
            throw new WinnerConflictException(BidErrorCode.WINNER_CONFLICT);
        }

        return ServiceResult.SUCCESS;
    }

    // TODO: 나중에 클래스로 분리할 예정
    private String makePayload(WinnerCreatedEvent event)  {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("이벤트 Payload JSON 변환 실패");
            throw new RuntimeException(e);
        }
    }
}
