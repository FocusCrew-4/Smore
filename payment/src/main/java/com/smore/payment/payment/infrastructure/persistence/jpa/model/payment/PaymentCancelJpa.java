package com.smore.payment.payment.infrastructure.persistence.jpa.model.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
public record PaymentCancelJpa(
        @Column(name = "cancel_reason")
        String reason,

        @Column(name = "cancel_amount")
        BigDecimal cancelAmount,

        @Column(name = "cancelled_at")
        LocalDateTime cancelledAt

) {
    public static PaymentCancelJpa of(
            String reason,
            BigDecimal amount,
            LocalDateTime occurredAt
    ) {
        return new PaymentCancelJpa(reason, amount, occurredAt);
    }
}
