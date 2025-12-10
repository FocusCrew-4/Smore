package com.smore.payment.payment.application.event.outbound;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PaymentApprovedEvent {

    private final UUID orderId;
    private final UUID paymentId;
    private final BigDecimal amount;
    private final LocalDateTime approvedAt;
    private final UUID idempotencyKey;

    public PaymentApprovedEvent(UUID orderId, UUID paymentId, BigDecimal amount, LocalDateTime approvedAt, UUID idempotencyKey) {
        this.orderId = orderId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.approvedAt = approvedAt;
        this.idempotencyKey = idempotencyKey;
    }

}
