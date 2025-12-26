package com.smore.payment.payment.infrastructure.kafka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRefundRequestEvent {
    private UUID orderId;
    private Long userId;
    private UUID refundId;
    private UUID paymentId;
    private Integer refundAmount;
    private UUID idempotencyKey;
    private String reason;
    private LocalDateTime publishedAt;
}
