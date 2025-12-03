package com.smore.order.infrastructure.persistence.entity.outbox;

import com.smore.order.domain.status.AggregateType;
import com.smore.order.domain.status.EventStatus;
import com.smore.order.domain.status.EventType;
import com.smore.order.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Table(
    name = "p_order_outbox",
    indexes = {
        @Index(name = "idx_outbox_status_created_at", columnList = "event_status, created_at")
    }
)
@Entity
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AggregateType aggregateType;

    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private UUID idempotencyKey;

    private String payload;

    private Integer retryCount;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    public static OutboxEntity create(
        AggregateType aggregateType,
        UUID aggregateId,
        EventType eventType,
        UUID idempotencyKey,
        String payload,
        Integer retryCount,
        EventStatus eventStatus
    ) {

        if (aggregateType == null) throw new IllegalArgumentException("aggregateType은 필수값입니다.");
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId는 필수값입니다.");
        if (eventType == null) throw new IllegalArgumentException("idempotencyKey는 필수값입니다.");
        if (payload == null) throw new IllegalArgumentException("payload는 필수값입니다.");

        return OutboxEntity.builder()
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .idempotencyKey(idempotencyKey)
            .payload(payload)
            .retryCount(retryCount)
            .eventStatus(eventStatus)
            .build();
    }

}
