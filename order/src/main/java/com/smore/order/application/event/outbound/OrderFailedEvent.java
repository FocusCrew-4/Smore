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
public class OrderFailedEvent implements OrderEvent {

    private final UUID allocationKey;
    private final UUID productId;
    private final Long userId;
    private final Integer quantity;
    private final LocalDateTime publishedAt;

    public static OrderFailedEvent of(
        UUID allocationKey,
        UUID productId,
        Long userId,
        Integer quantity,
        LocalDateTime now
    ) {
        return OrderFailedEvent.builder()
            .allocationKey(allocationKey)
            .productId(productId)
            .userId(userId)
            .quantity(quantity)
            .publishedAt(now)
            .build();
    }
}
