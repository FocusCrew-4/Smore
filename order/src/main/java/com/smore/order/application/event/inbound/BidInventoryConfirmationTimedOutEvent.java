package com.smore.order.application.event.inbound;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BidInventoryConfirmationTimedOutEvent {

    private UUID orderId;
    private Long userId;
    private Integer refundQuantity;
    private String reason;
    private UUID idempotencyKey;

}
