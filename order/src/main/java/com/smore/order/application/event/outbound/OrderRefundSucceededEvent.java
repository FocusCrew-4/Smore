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
public class OrderRefundSucceededEvent implements OrderEvent {
    private final UUID orderId;
    private final Long userId;
    private final UUID allocationKey;
    private final Integer refundAmount;
    private final LocalDateTime publishedAt;

    public static OrderRefundSucceededEvent of(
        UUID orderId, Long userId, UUID allocationKey, Integer refundAmount,
        LocalDateTime now) {

        return OrderRefundSucceededEvent.builder()
            .orderId(orderId)
            .userId(userId)
            .allocationKey(allocationKey)
            .refundAmount(refundAmount)
            .publishedAt(now)
            .build();
    }
}
