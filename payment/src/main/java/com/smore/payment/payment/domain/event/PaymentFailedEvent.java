package com.smore.payment.payment.domain.event;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentFailedEvent {

    private final UUID orderId;
    private final String paymentKey;
    private final String errorMessage;

    public PaymentFailedEvent(UUID orderId, String paymentKey, String errorMessage) {
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.errorMessage = errorMessage;
    }
}
