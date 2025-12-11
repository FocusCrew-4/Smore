package com.smore.payment.payment.infrastructure.pg.dto;

import java.math.BigDecimal;

public record TossCancelRequest(
        String cancelReason,
        BigDecimal cancelAmount
) {
}
