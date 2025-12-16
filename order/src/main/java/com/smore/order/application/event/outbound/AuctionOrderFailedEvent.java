package com.smore.order.application.event.outbound;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionOrderFailedEvent implements OrderEvent {
    private UUID productId;
    private Long userId;
    private UUID idempotencyKey;

    public static AuctionOrderFailedEvent of(UUID productId, Long userId, UUID idempotencyKey) {
        return AuctionOrderFailedEvent.builder()
            .productId(productId)
            .userId(userId)
            .idempotencyKey(idempotencyKey)
            .build();
    }
}
