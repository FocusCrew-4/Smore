package com.smore.payment.payment.infrastructure.persistence.jpa.model.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
public record PaymentRefundJpa(
        @Column(name = "refund_reason")
        String reason,

        @Column(name = "refund_amount")
        BigDecimal refundAmount,

        @Column(name = "refunded_at")
        LocalDateTime refundedAt,

        @Column(name = "pg_cancel_transaction_key", nullable = false)
        String pgCancelTransactionKey,

        @Column(name = "refundable_amount", nullable = false)
        BigDecimal refundableAmount
) {
    public static PaymentRefundJpa of(
            String reason,
            BigDecimal amount,
            LocalDateTime refundedAt,
            String pgCancelTransactionKey,
            BigDecimal refundableAmount
    ) {
        return new PaymentRefundJpa(reason, amount, refundedAt,  pgCancelTransactionKey, refundableAmount);
    }
}
