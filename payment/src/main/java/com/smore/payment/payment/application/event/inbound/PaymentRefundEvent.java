package com.smore.payment.payment.application.event.inbound;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentRefundEvent(
        UUID orderId,
        Long userId,
        UUID refundId,
        UUID paymentId,
        BigDecimal refundAmount,
        UUID idempotencyKey,
        String refundReason,
        LocalDateTime publishedAt
) {
    public static PaymentRefundEvent of(
            UUID orderId, Long userId, UUID refundId, UUID paymentId, BigDecimal refundAmount,
            UUID idempotencyKey, String refundReason, LocalDateTime publishedAt
    ) {
        return new PaymentRefundEvent(
                orderId,
                userId,
                refundId,
                paymentId,
                refundAmount,
                idempotencyKey,
                refundReason,
                publishedAt
        );
    }
}
