package com.smore.seller.infrastructure.kafka.listener.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SettlementV1(
    Long id,
    BigDecimal amount,
    UUID idempotencyKey
) {

}
