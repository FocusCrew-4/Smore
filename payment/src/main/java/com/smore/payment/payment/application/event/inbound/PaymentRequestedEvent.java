package com.smore.payment.payment.application.event.inbound;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PaymentRequestedEvent {

    private final UUID orderId;
    private final Long userId;
    private final BigDecimal amount;
    private final UUID categoryId;
    private final String auctionType;
    private final Long sellerId;
    private final UUID idempotencyKey;
    private final LocalDateTime publishedAt;
    private final LocalDateTime expiredAt;

    public PaymentRequestedEvent(
            UUID orderId,
            Long userId,
            BigDecimal amount,
            UUID idempotencyKey,
            Long sellerId,
            UUID categoryId,
            String auctionType,
            LocalDateTime publishedAt,
            LocalDateTime expiredAt
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.auctionType = auctionType;
        this.publishedAt = publishedAt;
        this.expiredAt = expiredAt;
    }

    public static PaymentRequestedEvent of(
            UUID orderId,
            Long userId,
            BigDecimal amount,
            UUID idempotencyKey,
            Long sellerId,
            UUID categoryId,
            String auctionType,
            LocalDateTime publishedAt,
            LocalDateTime expiredAt
    ) {
        return new PaymentRequestedEvent(
                orderId,
                userId,
                amount,
                idempotencyKey,
                sellerId,
                categoryId,
                auctionType,
                publishedAt,
                expiredAt
        );
    }
}
