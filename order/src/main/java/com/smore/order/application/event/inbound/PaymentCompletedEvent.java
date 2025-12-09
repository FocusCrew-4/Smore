package com.smore.order.application.event.inbound;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCompletedEvent {
    private UUID orderId;
    private String paymentId;
    private BigInteger amount;
    private LocalDateTime approvedAt;
    private UUID idempotencyKey;
}
