package com.smore.order.infrastructure.persistence.mapper;

import com.smore.order.domain.model.Outbox;
import com.smore.order.infrastructure.persistence.entity.outbox.OutboxEntity;

public final class OutboxMapper {

    private OutboxMapper() {

    }

    public static OutboxEntity toEntityForCreate(Outbox outbox) {
        if (outbox == null) {
            return null;
        }

        return OutboxEntity.create(
            outbox.getAggregateType(),
            outbox.getAggregateId(),
            outbox.getEventType(),
            outbox.getIdempotencyKey(),
            outbox.getPayload(),
            outbox.getEventStatus()
        );
    }

    public static Outbox toDomain(OutboxEntity entity) {
        if (entity == null) {
            return null;
        }

        return Outbox.of(
            entity.getId(),
            entity.getAggregateType(),
            entity.getAggregateId(),
            entity.getEventType(),
            entity.getIdempotencyKey(),
            entity.getPayload(),
            entity.getEventStatus()
        );
    }
}
