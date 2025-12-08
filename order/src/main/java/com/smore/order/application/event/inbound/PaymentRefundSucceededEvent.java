package com.smore.order.application.event.inbound;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRefundSucceededEvent {
    private UUID orderId;
    private UUID refundId;
    private Integer refundAmount;

}
