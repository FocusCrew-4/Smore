package com.smore.payment.payment.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record ApprovePaymentCommand(
        UUID orderId,
        UUID idempotencyKey,
        BigDecimal amount,
        String paymentKey,
        String pgOrderId
) {
}
