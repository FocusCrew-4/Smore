package com.smore.order.application.repository;

import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.EventStatus;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutboxRepository {

    Outbox save(Outbox outbox);

    Outbox findById(Long outboxId);

    int claim(Long outboxId, EventStatus eventStatus);

    int markSent(Long outboxId, EventStatus eventStatus);

    int makeRetry(Long outboxId, EventStatus eventStatus);

    int makeFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount);

    Page<Long> findPendingIds(Collection<EventStatus> states, Pageable pageable);
}
