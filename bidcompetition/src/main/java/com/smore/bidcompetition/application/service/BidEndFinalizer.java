package com.smore.bidcompetition.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.bidcompetition.application.repository.BidCompetitionRepository;
import com.smore.bidcompetition.application.repository.OutboxRepository;
import com.smore.bidcompetition.domain.model.BidCompetition;
import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.domain.status.AggregateType;
import com.smore.bidcompetition.domain.status.EventType;
import com.smore.bidcompetition.infrastructure.persistence.event.outbound.BidEvent;
import com.smore.bidcompetition.infrastructure.persistence.event.outbound.BidResultFinalizedEvent;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "BidEndFinalizer")
@Service
@RequiredArgsConstructor
public class BidEndFinalizer {


    private final BidCompetitionRepository bidCompetitionRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.allocation.close-grace-seconds}")
    private long closeGraceSeconds;

    @Transactional
    public void finalizeBid(UUID bidId, LocalDateTime now) {

        int updated = bidCompetitionRepository.finalizeByValidAt(
            bidId,
            now,
            closeGraceSeconds
        );

        if (updated == 0) {
            return;
        }

        BidCompetition bid = bidCompetitionRepository.findById(bidId);

        int soldQuantity = bid.getTotalQuantity() - bid.getStock();

        BidResultFinalizedEvent event = BidResultFinalizedEvent.of(
            bid.getId(),
            bid.getProductId(),
            soldQuantity,
            bid.getIdempotencyKey(),
            bid.getStartAt(),
            bid.getEndAt(),
            now
        );

        Outbox outbox = Outbox.create(
            AggregateType.BID,
            bid.getId(),
            EventType.BID_RESULT_FINALIZED,
            UUID.randomUUID(),
            makePayload(event)
        );

        outboxRepository.save(outbox);
    }

    private String makePayload(BidEvent event)  {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("이벤트 Payload JSON 변환 실패");
            throw new RuntimeException(e);
        }
    }

}
