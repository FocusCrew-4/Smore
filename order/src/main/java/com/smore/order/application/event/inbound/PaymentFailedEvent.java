package com.smore.order.application.event.inbound;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentFailedEvent {
    // TODO: 수정 필요 - orderId랑 errorMessage만 있으면 됨
    private UUID orderId;
    private UUID paymentId;
    private String errorMessage;
    private UUID idempotencyKey;
}
