package com.smore.payment.payment.application.event.outbound;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentFailedEvent {

    private final UUID orderId;
    private final UUID paymentId;
    private final String errorMessage;
    private final UUID idempotencyKey;

    public PaymentFailedEvent(UUID orderId, UUID paymentId, String errorMessage, UUID idempotencyKey) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.errorMessage = errorMessage;
        this.idempotencyKey = idempotencyKey;
    }
}
