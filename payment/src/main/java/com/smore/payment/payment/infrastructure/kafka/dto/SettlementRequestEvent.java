package com.smore.payment.payment.infrastructure.kafka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class SettlementRequestEvent {

    private Long userId;
    private BigDecimal amount;
    private String accountNumber;
    private UUID idempotencyKey;
    private LocalDateTime createdAt;
}
