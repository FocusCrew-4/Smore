package com.smore.payment.payment.domain.model;

import java.time.LocalDateTime;

public record PaymentFailure(
        String reason,
        LocalDateTime failedAt
) {
    public PaymentFailure {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("결제 실패 사유는 필수입니다.");
        }
        if (failedAt == null) {
            throw new IllegalArgumentException("결제 실패 시간은 null일 수 없습니다.");
        }
    }

    public static PaymentFailure of(String reason, LocalDateTime failedAt) {
        return new PaymentFailure(reason, failedAt);
    }
}
