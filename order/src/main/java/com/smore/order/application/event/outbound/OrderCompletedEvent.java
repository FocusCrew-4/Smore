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
public class OrderCompletedEvent implements OrderEvent {

    private final UUID orderId;
    private final Long userId;
    private final OrderStatus currentOrderStatus;
    private final UUID idempotencyKey;
    private final LocalDateTime publishedAt;

    public static OrderCompletedEvent of(
        UUID orderId, Long userId, OrderStatus currentOrderStatus, UUID idempotencyKey,
        LocalDateTime now) {

        return OrderCompletedEvent.builder()
            .orderId(orderId)
            .userId(userId)
            .currentOrderStatus(currentOrderStatus)
            .idempotencyKey(idempotencyKey)
            .publishedAt(now)
            .build();
    }

}
