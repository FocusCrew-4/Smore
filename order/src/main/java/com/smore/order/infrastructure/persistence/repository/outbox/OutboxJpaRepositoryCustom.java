package com.smore.order.infrastructure.persistence.repository.outbox;

import com.smore.order.domain.status.EventStatus;

public interface OutboxJpaRepositoryCustom {

    int claim(Long outboxId, EventStatus eventStatus);

    int markSent(Long outboxId, EventStatus eventStatus);

    int makeRetry(Long outboxId, EventStatus eventStatus);

    int makeFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount);

}
