package com.smore.bidcompetition.infrastructure.persistence.event.outbound;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryConfirmationTimedOutEvent implements BidEvent{

    private UUID orderId;
    private Long userId;
    private Integer refundQuantity;
    private String reason;
    private UUID idempotencyKey;

}
