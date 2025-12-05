package com.smore.payment.payment.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
public record PaymentCancelJpa(
        @Column(name = "cancel_reason")
        String reason,

        @Column(name = "cancel_amount")
        BigDecimal amount,

        @Column(name = "cancelled_at")
        LocalDateTime occurredAt,

        @Column(name = "pg_cancel_transaction_id")
        String pgCancelTransactionId
) {
    public static PaymentCancelJpa of(
            String reason,
            BigDecimal amount,
            LocalDateTime occurredAt,
            String pgCancelTransactionId
    ) {
        return new PaymentCancelJpa(reason, amount, occurredAt, pgCancelTransactionId);
    }
}
