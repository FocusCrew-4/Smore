package com.smore.payment.payment.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
public record PaymentRefundJpa(
        @Column(name = "refund_reason")
        String reason,

        @Column(name = "refund_amount")
        BigDecimal amount,

        @Column(name = "refunded_at")
        LocalDateTime occurredAt
) {
    public static PaymentRefundJpa of(
            String reason,
            BigDecimal amount,
            LocalDateTime occurredAt
    ) {
        return new PaymentRefundJpa(reason, amount, occurredAt);
    }
}
