package com.smore.order.application.event.outbound;

import com.smore.order.domain.status.OrderStatus;
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
    private final UUID refundId;
    private final Long userId;
    private final Integer quantity;
    private final UUID allocationKey;
    private final Integer refundAmount;
    private final String status;
    private final LocalDateTime publishedAt;

    public static OrderRefundSucceededEvent of(
        UUID orderId, UUID refundId, Long userId, Integer quantity,
        UUID allocationKey, Integer refundAmount, OrderStatus status,
        LocalDateTime now) {

        return OrderRefundSucceededEvent.builder()
            .orderId(orderId)
            .refundId(refundId)
            .userId(userId)
            .quantity(quantity)
            .allocationKey(allocationKey)
            .refundAmount(refundAmount)
            .status(String.valueOf(status))
            .publishedAt(now)
            .build();
    }
}
