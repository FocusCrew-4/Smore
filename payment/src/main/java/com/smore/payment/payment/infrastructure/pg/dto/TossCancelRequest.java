package com.smore.payment.payment.infrastructure.pg.dto;

import java.math.BigDecimal;

public record TossCancelRequest(
        BigDecimal refundAmount,
        String refundReason
) {
}
