package com.smore.payment.payment.application.event.outbound;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentApprovedEvent(UUID orderId, UUID paymentId, BigDecimal amount, LocalDateTime approvedAt,
                                   UUID idempotencyKey) {

}
