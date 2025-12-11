package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRefund(
        String reason,
        BigDecimal cancelAmount,
        LocalDateTime refundedAt,
        String pgCancelTransactionKey,
        BigDecimal refundableAmount
) {

    public PaymentRefund {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("환불 사유는 필수입니다.");
        }
        if (cancelAmount == null || cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("환불 금액은 0보다 커야 합니다.");
        }
        if (refundedAt == null) {
            throw new IllegalArgumentException("환불 시간은 null일 수 없습니다.");
        }
    }

    public static PaymentRefund of(String reason, BigDecimal refundAmount, LocalDateTime refundedAt, String pgCancelTransactionKey, BigDecimal refundableAmount) {
        return new PaymentRefund(reason, refundAmount, refundedAt,  pgCancelTransactionKey, refundableAmount);
    }
}
