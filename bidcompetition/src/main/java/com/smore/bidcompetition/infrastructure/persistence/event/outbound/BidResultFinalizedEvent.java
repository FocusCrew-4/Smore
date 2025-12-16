package com.smore.bidcompetition.infrastructure.persistence.event.outbound;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidResultFinalizedEvent implements BidEvent {
    private UUID bidId;
    private UUID productId;
    private Integer soldQuantity;
    private UUID idempotencykey;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;

    public static BidResultFinalizedEvent of(
        UUID bidId,
        UUID productId,
        Integer soldQuantity,
        UUID idempotencykey,
        LocalDateTime startAt,
        LocalDateTime endAt,
        LocalDateTime createdAt
    ) {
        return BidResultFinalizedEvent.builder()
            .bidId(bidId)
            .productId(productId)
            .soldQuantity(soldQuantity)
            .idempotencykey(idempotencykey)
            .startAt(startAt)
            .endAt(endAt)
            .createdAt(createdAt)
            .build();
    }
}
