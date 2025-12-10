package com.smore.order.application.event.inbound;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentFailedEvent {
    private UUID orderId;
    private UUID paymentId;
    private String errorMessage;
    private UUID idempotencyKey;
}
