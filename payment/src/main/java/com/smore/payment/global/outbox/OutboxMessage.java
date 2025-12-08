package com.smore.payment.global.outbox;

import com.smore.payment.payment.domain.event.PaymentApprovedEvent;
import com.smore.payment.payment.domain.event.PaymentFailedEvent;
import com.smore.payment.global.util.JsonUtil;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OutboxMessage {

    private Long id;

    private final String aggregateType;     // ex: PAYMENT, ORDER
    private final String aggregateId;       // ex: paymentId, orderId
    private final String eventType;         // ex: PaymentApprovedEvent
    private final String payload;           // JSON
    private OutboxStatus status;            // PENDING / SENT
    private final LocalDateTime createdAt;

    public OutboxMessage(
            String aggregateType,
            String aggregateId,
            String eventType,
            String payload,
            OutboxStatus status
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public static OutboxMessage paymentApproved(PaymentApprovedEvent event) {
        return new OutboxMessage(
                "PAYMENT",
                event.getPaymentId().toString(),
                event.getClass().getSimpleName(),
                JsonUtil.jsonToString(event),
                OutboxStatus.PENDING
        );
    }

    public static OutboxMessage paymentFailed(PaymentFailedEvent event) {
        return new OutboxMessage(
                "PAYMENT",
                event.getOrderId().toString(),
                event.getClass().getSimpleName(),
                JsonUtil.jsonToString(event),
                OutboxStatus.PENDING
        );
    }

    public void markAsSent() {
        this.status = OutboxStatus.SENT;
    }
}
