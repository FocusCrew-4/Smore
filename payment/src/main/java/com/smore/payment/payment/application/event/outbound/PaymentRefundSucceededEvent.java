package com.smore.payment.payment.application.event.outbound;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefundSucceededEvent(
        UUID orderId,
        UUID refundId,
        BigDecimal refundAmount
) {
    public static PaymentRefundSucceededEvent of(UUID orderId, UUID refundId, BigDecimal refundAmount) {
        return new PaymentRefundSucceededEvent(
                orderId,
                refundId,
                refundAmount
        );
    }
}
