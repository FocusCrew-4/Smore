package com.smore.bidcompetition.infrastructure.persistence.event.outbound;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidProductInventoryAdjustedEvent implements BidEvent {
    private UUID bidId;
    private UUID productId;
    private Integer refundQuantity;
    private UUID idempotencyKey;

    public static BidProductInventoryAdjustedEvent of(
        UUID bidId,
        UUID productId,
        Integer refundQuantity,
        UUID idempotencyKey
    ) {
        return BidProductInventoryAdjustedEvent.builder()
            .bidId(bidId)
            .productId(productId)
            .refundQuantity(refundQuantity)
            .idempotencyKey(idempotencyKey)
            .build();
    }
}
