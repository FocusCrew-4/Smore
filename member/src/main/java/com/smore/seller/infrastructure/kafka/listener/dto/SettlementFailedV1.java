package com.smore.seller.infrastructure.kafka.listener.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SettlementFailedV1(
    Long id,
    BigDecimal amount,
    String accountNum,
    String failedReason,
    UUID idempotencyKey,
    LocalDateTime createdAt
) {
}
