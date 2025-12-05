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
public class RefundRequestEvent implements OrderEvent {

    private final UUID orderId;
    private final Long userId;
    private final UUID refundId;
    private final Integer refundAmount;
    private final UUID idempotencyKey;
    private final LocalDateTime publishedAt;

    public static RefundRequestEvent of(
        UUID orderId, Long userId,
        UUID refundId, Integer refundAmount,
        UUID idempotencyKey,
        LocalDateTime publishedAt
    ) {

        return RefundRequestEvent.builder()
            .orderId(orderId)
            .userId(userId)
            .refundId(refundId)
            .refundAmount(refundAmount)
            .idempotencyKey(idempotencyKey)
            .publishedAt(publishedAt)
            .build();
    }
}
