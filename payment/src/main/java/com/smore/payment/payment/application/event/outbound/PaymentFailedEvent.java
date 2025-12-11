package com.smore.payment.payment.application.event.outbound;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentFailedEvent {

    private final UUID orderId;
    private final String errorMessage;

    public PaymentFailedEvent(UUID orderId, String errorMessage) {
        this.orderId = orderId;
        this.errorMessage = errorMessage;
    }

}
