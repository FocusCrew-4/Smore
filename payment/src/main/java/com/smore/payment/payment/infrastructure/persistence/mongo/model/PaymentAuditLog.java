package com.smore.payment.payment.infrastructure.persistence.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payment_audit_logs")
public class PaymentAuditLog {

    @Id
    private String id;

    @Indexed
    private UUID orderId;

    @Indexed
    private UUID paymentId;

    private UUID refundId;

    private UUID idempotencyKey;

    private Long userId;

    private Long sellerId;

    private UUID categoryId;

    private String auctionType;

    private BigDecimal amount;

    private String paymentKey;

    private String pgOrderId;

    private PaymentAuditEventType eventType;

    private String description;

    @Builder.Default
    private LocalDateTime occurredAt = LocalDateTime.now();
}