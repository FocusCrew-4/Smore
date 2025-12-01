package com.smore.order.application.repository;

import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.EventStatus;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    Outbox findById(Long outboxId);

    int claim(Long outboxId, EventStatus eventStatus);

    int markSent(Long outboxId, EventStatus eventStatus);

    int makeRetry(Long outboxId, EventStatus eventStatus);

    int makeFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount);

}
