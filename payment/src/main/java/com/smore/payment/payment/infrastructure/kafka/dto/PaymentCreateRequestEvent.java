package com.smore.payment.payment.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentCreateRequestEvent {
    private final UUID orderId;
    private final Long userId;
    private final Integer totalAmount;
    private final UUID categoryId;
    private final String saleType;
    private final Long sellerId;
    private final UUID idempotencyKey;
    private final LocalDateTime publishedAt;
    private final LocalDateTime expiresAt;
}
