package com.smore.payment.payment.application.event.outbound;

import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.domain.model.Payment;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefundFailedEvent(
        UUID orderId,
        UUID refundId,
        BigDecimal refundAmount,
        String reason
) {

    public static PaymentRefundFailedEvent of(UUID orderId, UUID refundId, BigDecimal refundAmount, String message) {
        return new PaymentRefundFailedEvent(
                orderId,
                refundId,
                refundAmount,
                message
        );
    }
}
