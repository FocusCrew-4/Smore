package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentCancel(
        String reason,
        BigDecimal cancelAmount,
        LocalDateTime cancelledAt
) {

    public PaymentCancel {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("취소 사유는 필수입니다.");
        }
        if (cancelAmount == null || cancelAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("취소 금액은 0보다 커야 합니다.");
        }
        if (cancelledAt == null) {
            throw new IllegalArgumentException("취소 일시는 null일 수 없습니다.");
        }
    }

    public static PaymentCancel of(String reason, BigDecimal cancelAmount, LocalDateTime canceledAt) {
        return new PaymentCancel(reason, cancelAmount, canceledAt);
    }
}
