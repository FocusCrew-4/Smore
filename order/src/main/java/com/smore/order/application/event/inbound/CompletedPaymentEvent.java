package com.smore.order.application.event.inbound;

import java.util.UUID;
import lombok.Getter;

@Getter
public class CompletedPaymentEvent {
    // TODO: Payment와 협의하여 어떤 데이터를 줄 것인지에 따라 구현해야 함
    private UUID orderId;

}
