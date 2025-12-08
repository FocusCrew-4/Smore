package com.smore.payment.global.outbox;

import com.smore.payment.payment.domain.event.PaymentApprovedEvent;
import com.smore.payment.payment.domain.event.PaymentFailedEvent;
import com.smore.payment.global.util.JsonUtil;
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
    private final String payload;           // JSON
    private final Integer retryCount;
    private OutboxStatus status;            // PENDING / SENT
    private final LocalDateTime createdAt;

    public OutboxMessage(
            String aggregateType,
            UUID aggregateId,
            String eventType,
            UUID idempotencyKey,
            String payload,
            Integer retryCount,
            OutboxStatus status
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.idempotencyKey = idempotencyKey;
        this.payload = payload;
        this.retryCount = retryCount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static OutboxMessage paymentApproved(PaymentApprovedEvent event) {
        return new OutboxMessage(
                "PAYMENT",
                event.getPaymentId(),
                event.getClass().getSimpleName(),
                event.getIdempotencyKey(),
                JsonUtil.jsonToString(event),
                3,
                OutboxStatus.PENDING
        );
    }

    public static OutboxMessage paymentFailed(PaymentFailedEvent event) {
        return new OutboxMessage(
                "PAYMENT",
                event.getOrderId(),
                event.getClass().getSimpleName(),
                event.getIdempotencyKey(),
                JsonUtil.jsonToString(event),
                3,
                OutboxStatus.PENDING
        );
    }

    public void markAsSent() {
        this.status = OutboxStatus.SENT;
    }
}
