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

    private Integer retryCount;

    private EventStatus eventStatus;

    private String traceId;

    private String spanId;

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
            .retryCount(0)
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
        Integer retryCount,
        EventStatus eventStatus,
        String traceId,
        String spanId
    ) {

        return Outbox.builder()
            .id(id)
            .aggregateType(aggregateType)
            .aggregateId(aggregateId)
            .eventType(eventType)
            .idempotencyKey(idempotencyKey)
            .payload(payload)
            .retryCount(retryCount)
            .eventStatus(eventStatus)
            .traceId(traceId)
            .spanId(spanId)
            .build();
    }

    public void attachTracing(String traceId, String spanId) {
        this.traceId = traceId;
        this.spanId = spanId;
    }

    public boolean isExceededRetry(Integer maxRetryCount) {
        return retryCount >= maxRetryCount;
    }

}
