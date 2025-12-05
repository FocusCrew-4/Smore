package com.smore.order.application.event.outbound;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreatedOrderEvent implements OrderEvent {

    private final UUID orderId;
    private final Long userId;
    private final Integer totalAmount;
    private final UUID idempotencyKey;
    private final LocalDateTime publishedAt;
    private final LocalDateTime expiresAt;

    public static CreatedOrderEvent of(
        UUID orderId, Long userId, Integer totalAmount, UUID idempotencyKey,
        LocalDateTime now, LocalDateTime expiresAt) {

        return CreatedOrderEvent.builder()
            .orderId(orderId)
            .userId(userId)
            .totalAmount(totalAmount)
            .idempotencyKey(idempotencyKey)
            .publishedAt(now)
            .expiresAt(expiresAt)
            .build();
    }
}
