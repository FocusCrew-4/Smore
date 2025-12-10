package com.smore.order.application.event.outbound;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// paymentId 추가
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderRefundRequestEvent implements OrderEvent {

    private final UUID orderId;
    private final Long userId;
    private final UUID refundId;
    private final UUID paymentId;
    private final Integer refundAmount;
    private final UUID idempotencyKey;
    private final String reason;
    private final LocalDateTime publishedAt;

    public static OrderRefundRequestEvent of(
        UUID orderId, Long userId,
        UUID refundId, UUID paymentId, Integer refundAmount,
        UUID idempotencyKey,
        String reason,
        LocalDateTime publishedAt
    ) {

        return OrderRefundRequestEvent.builder()
            .orderId(orderId)
            .userId(userId)
            .refundId(refundId)
            .paymentId(paymentId)
            .refundAmount(refundAmount)
            .idempotencyKey(idempotencyKey)
            .reason(reason)
            .publishedAt(publishedAt)
            .build();
    }
}
