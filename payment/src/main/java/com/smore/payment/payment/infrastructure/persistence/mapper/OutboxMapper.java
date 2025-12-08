package com.smore.payment.payment.infrastructure.persistence.mapper;

import com.smore.payment.global.outbox.OutboxMessage;
import com.smore.payment.payment.infrastructure.persistence.model.outbox.OutboxEntity;
import org.springframework.stereotype.Component;

@Component
public class OutboxMapper {

    public OutboxEntity toEntity(OutboxMessage message) {
        return new OutboxEntity(
                message.getAggregateType(),
                message.getAggregateId(),
                message.getEventType(),
                message.getIdempotencyKey(),
                message.getPayload(),
                message.getRetryCount(),
                message.getStatus(),
                message.getCreatedAt()
        );
    }

    public OutboxMessage toDomain(OutboxEntity entity) {
        return new OutboxMessage(
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getIdempotencyKey(),
                entity.getPayload(),
                entity.getRetryCount(),
                entity.getStatus()
        );
    }
}
