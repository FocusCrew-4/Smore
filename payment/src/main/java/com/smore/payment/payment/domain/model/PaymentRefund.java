package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRefund(String reason, BigDecimal refundAmount, LocalDateTime refundedAt) {
    public static PaymentRefund of(String reason, BigDecimal refundAmount, LocalDateTime refundedAt) {
        return new PaymentRefund(reason, refundAmount, refundedAt);
    }
}
