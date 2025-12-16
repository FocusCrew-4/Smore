package com.smore.payment.payment.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ApprovePaymentResult(
        UUID paymentId,
        UUID orderId,
        BigDecimal approvedAmount,
        LocalDateTime approvedAt,
        String status
) {
}