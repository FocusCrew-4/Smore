package com.smore.payment.payment.application.event.inbound;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentRequestedEvent(UUID orderId, Long userId, BigDecimal amount, UUID idempotencyKey, Long sellerId,
                                    UUID categoryId, String auctionType, LocalDateTime publishedAt,
                                    LocalDateTime expiredAt) {

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
