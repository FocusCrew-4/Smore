package com.smore.payment.payment.application.event.outbound;

import java.math.BigDecimal;
import java.util.UUID;

public record SettlementCalculatedEvent(
        Long sellerId,
        BigDecimal settlementAmount,
        UUID idempotencyKey
) {}
