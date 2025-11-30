package com.smore.order.application.repository;

import com.smore.order.domain.model.Outbox;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    Outbox findById(Long outboxId);

}
