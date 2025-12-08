package com.smore.payment.payment.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record ApprovePaymentCommand(
        UUID orderId,
        BigDecimal amount,
        String paymentKey,
        String pgOrderId
        ) {
}
