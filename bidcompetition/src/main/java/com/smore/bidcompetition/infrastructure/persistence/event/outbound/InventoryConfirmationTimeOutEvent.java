package com.smore.bidcompetition.infrastructure.persistence.event.outbound;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryConfirmationTimeOutEvent implements BidEvent{

    private UUID orderId;
    private Long userId;
    private Integer refundQuantity;
    private String reason;
    private UUID idempotencyKey;

    public static InventoryConfirmationTimeOutEvent of(
        UUID orderId,
        Long userId,
        Integer refundQuantity,
        String reason,
        UUID idempotencyKey
    ) {
        return InventoryConfirmationTimeOutEvent.builder()
            .orderId(orderId)
            .userId(userId)
            .refundQuantity(refundQuantity)
            .reason(reason)
            .idempotencyKey(idempotencyKey)
            .build();
    }

}
