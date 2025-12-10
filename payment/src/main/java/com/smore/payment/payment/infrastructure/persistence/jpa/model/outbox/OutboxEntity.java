package com.smore.payment.payment.infrastructure.persistence.jpa.model.outbox;

import com.smore.payment.global.outbox.OutboxStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outboxes")
@Getter
@NoArgsConstructor
public class OutboxEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private UUID aggregateId;
    private String eventType;

    private UUID idempotencyKey;

    @Lob
    private String payload;

    private Integer retryCount;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    private LocalDateTime createdAt;

    public OutboxEntity(
            String aggregateType,
            UUID aggregateId,
            String eventType,
            UUID idempotencyKey,
            String payload,
            Integer retryCount,
            OutboxStatus status,
            LocalDateTime createdAt
    ) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.idempotencyKey = idempotencyKey;
        this.payload = payload;
        this.retryCount = retryCount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void markAsSent() {
        this.status = OutboxStatus.SENT;
    }

    public void markAsFailed() {
        this.status = OutboxStatus.FAILED;
    }

    public void decreaseRetryCount() {
        this.retryCount--;
    }
}
