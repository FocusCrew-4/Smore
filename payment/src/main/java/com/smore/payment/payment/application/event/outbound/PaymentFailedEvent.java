package com.smore.payment.payment.application.event.outbound;

import java.util.UUID;

public record PaymentFailedEvent(UUID orderId, String errorMessage) {

}
