package com.smore.order.application.event.outbound;

import com.smore.order.domain.status.SaleType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderCreatedEvent implements OrderEvent {

    private final UUID orderId;
    private final Long userId;
    private final Integer totalAmount;
    private final UUID categoryId;
    private final String saleType;
    private Long sellerId;
    private final UUID idempotencyKey;
    private final LocalDateTime publishedAt;
    private final LocalDateTime expiresAt;

    public static OrderCreatedEvent of(
        UUID orderId, Long userId, Integer totalAmount,
        UUID categoryId, SaleType saleType, Long sellerId,
        UUID idempotencyKey,
        LocalDateTime now, LocalDateTime expiresAt) {

        return OrderCreatedEvent.builder()
            .orderId(orderId)
            .userId(userId)
            .totalAmount(totalAmount)
            .categoryId(categoryId)
            .saleType(String.valueOf(saleType))
            .sellerId(sellerId)
            .idempotencyKey(idempotencyKey)
            .publishedAt(now)
            .expiresAt(expiresAt)
            .build();
    }
}
