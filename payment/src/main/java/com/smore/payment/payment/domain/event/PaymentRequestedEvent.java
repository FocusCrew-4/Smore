package com.smore.payment.payment.domain.event;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PaymentRequestedEvent {

    private final UUID orderId;
    private final Long userId;
    private final BigDecimal amount;
    private final UUID idempotencyKey;
    private final LocalDateTime publishedAt;
    private final LocalDateTime expiredAt;

    public PaymentRequestedEvent(
            UUID orderId,
            Long userId,
            BigDecimal amount,
            UUID idempotencyKey,
            LocalDateTime publishedAt,
            LocalDateTime expiredAt
    ) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.idempotencyKey = idempotencyKey;
        this.publishedAt = publishedAt;
        this.expiredAt = expiredAt;
    }
}
