package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRefund(
        String reason,
        BigDecimal refundAmount,
        LocalDateTime refundedAt
) {

    public PaymentRefund {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("환불 사유는 필수입니다.");
        }
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("환불 금액은 0보다 커야 합니다.");
        }
        if (refundedAt == null) {
            throw new IllegalArgumentException("환불 시간은 null일 수 없습니다.");
        }
    }

    public static PaymentRefund of(String reason, BigDecimal refundAmount, LocalDateTime refundedAt) {
        return new PaymentRefund(reason, refundAmount, refundedAt);
    }
}
