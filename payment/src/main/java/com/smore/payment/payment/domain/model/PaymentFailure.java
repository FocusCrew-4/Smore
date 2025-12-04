package com.smore.payment.payment.domain.model;

import java.time.LocalDateTime;

public record PaymentFailure(String reason, LocalDateTime failedAt) {
    public static PaymentFailure of(String reason, LocalDateTime failedAt) {
        return new PaymentFailure(reason, failedAt);
    }
}
