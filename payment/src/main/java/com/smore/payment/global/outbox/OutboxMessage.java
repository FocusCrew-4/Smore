package com.smore.payment.global.outbox;

import com.smore.payment.payment.application.event.outbound.PaymentApprovedEvent;
import com.smore.payment.payment.application.event.outbound.PaymentFailedEvent;
import com.smore.payment.global.util.JsonUtil;
import com.smore.payment.payment.application.event.outbound.SettlementCalculatedEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class OutboxMessage {

    private Long id;

    private final String aggregateType;     // ex: PAYMENT, ORDER
    private final UUID aggregateId;       // ex: paymentId, orderId
    private final String eventType;         // ex: PaymentApprovedEvent
    private final UUID idempotencyKey;
    private final String topicName;
    private final String payload;           // JSON
    private final Integer retryCount;
    private OutboxStatus status;            // PENDING / SENT
    private final LocalDateTime createdAt;

    public OutboxMessage(
            String aggregateType,
            UUID aggregateId,
            String eventType,
            UUID idempotencyKey,
            String topicName,
            String payload,
            Integer retryCount,
            OutboxStatus status
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.idempotencyKey = idempotencyKey;
        this.topicName = topicName;
        this.payload = payload;
        this.retryCount = retryCount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

}
