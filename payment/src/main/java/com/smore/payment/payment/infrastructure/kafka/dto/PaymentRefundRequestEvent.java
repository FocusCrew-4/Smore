package com.smore.payment.payment.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PaymentRefundRequestEvent {
    private final UUID orderId;
    private final Long userId;
    private final UUID refundId;
    private final UUID paymentId;
    private final Integer refundAmount;
    private final UUID idempotencyKey;
    private final String reason;
    private final LocalDateTime publishedAt;
}
