package com.smore.payment.payment.infrastructure.kafka.dto;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
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

    public PaymentCreateRequestEvent(
            UUID orderId,
            Long userId,
            Integer totalAmount,
            UUID idempotencyKey,
            Long sellerId,
            UUID categoryId,
            String saleType,
            LocalDateTime publishedAt,
            LocalDateTime expiresAt
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.idempotencyKey = idempotencyKey;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.saleType = saleType;
        this.publishedAt = publishedAt;
        this.expiresAt = expiresAt;
    }
}
