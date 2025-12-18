package com.smore.bidcompetition.infrastructure.persistence.mapper;

import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.infrastructure.persistence.entity.OutboxEntity;

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
            outbox.getRetryCount(),
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
            entity.getRetryCount(),
            entity.getEventStatus()
        );
    }
}
