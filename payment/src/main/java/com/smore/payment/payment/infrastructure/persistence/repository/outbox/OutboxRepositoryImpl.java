package com.smore.payment.payment.infrastructure.persistence.repository.outbox;

import com.smore.payment.global.outbox.OutboxMessage;
import com.smore.payment.payment.domain.repository.OutboxRepository;

public class OutboxRepositoryImpl implements OutboxRepository {
    @Override
    public void save(OutboxMessage outboxMessage) {

    }
}
