package com.smore.payment.payment.infrastructure.kafka.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCreateRequestEvent {
    private UUID orderId;
    private Long userId;
    private Integer totalAmount;
    private UUID categoryId;
    private String saleType;
    private Long sellerId;
    private UUID idempotencyKey;
    private LocalDateTime publishedAt;
    private LocalDateTime expiresAt;
}
