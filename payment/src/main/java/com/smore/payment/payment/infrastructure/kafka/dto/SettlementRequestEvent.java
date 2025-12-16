package com.smore.payment.payment.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SettlementRequestEvent {

    private final Long userId;
    private final BigDecimal amount;
    private final String accountNumber;
    private final UUID idempotencyKey;
    private final LocalDateTime createdAt;
}
