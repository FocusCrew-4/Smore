package com.smore.order.domain.model;

import com.smore.order.domain.status.AggregateType;
import com.smore.order.domain.status.EventStatus;
import com.smore.order.domain.status.EventType;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Outbox {

    private Long id;

    private AggregateType aggregateType;

    private UUID aggregateId;

    private EventType eventType;

    private UUID idempotencyKey;

    private String payload;

    private EventStatus eventStatus;

    public static Outbox create(
        AggregateType aggregateType,
        UUID aggregateId,
        EventType eventType,
        UUID idempotencyKey,
        String payload
    ) {
        if (aggregateType == null) throw new IllegalArgumentException("aggregateType은 필수값입니다.");
        if (aggregateId == null) throw new IllegalArgumentException("aggregateId는 필수값입니다.");
        if (eventType == null) throw new IllegalArgumentException("eventType은 필수값입니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("idempotencyKey는 필수값입니다.");
        if (payload == null) throw new IllegalArgumentException("payload는 필수값입니다.");

        return Outbox.builder()
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .idempotencyKey(idempotencyKey)
            .payload(payload)
            .eventStatus(EventStatus.PENDING)
            .build();
    }

    public static Outbox of(
        Long id,
        AggregateType aggregateType,
        UUID aggregateId,
        EventType eventType,
        UUID idempotencyKey,
        String payload,
        EventStatus eventStatus
    ) {

        return Outbox.builder()
            .id(id)
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .idempotencyKey(idempotencyKey)
            .payload(payload)
            .eventStatus(eventStatus)
            .build();
    }

}
