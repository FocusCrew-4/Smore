package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentCancel(String reason, BigDecimal cancelAmount, LocalDateTime canceledAt, String pgCancelTransactionId) {
    public static PaymentCancel of(String reason, BigDecimal cancelAmount, LocalDateTime canceledAt, String pgCancelTransactionId) {
        return new PaymentCancel(reason, cancelAmount, canceledAt, pgCancelTransactionId);
    }
}
