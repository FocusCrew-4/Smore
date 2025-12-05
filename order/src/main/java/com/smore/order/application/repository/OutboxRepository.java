package com.smore.order.application.repository;

import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.EventStatus;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutboxRepository {

    Outbox findById(Long outboxId);

    Page<Long> findPendingIds(Collection<EventStatus> states, Pageable pageable);

    Outbox save(Outbox outbox);

    int claim(Long outboxId, EventStatus eventStatus);

    int markSent(Long outboxId, EventStatus eventStatus);

    int markRetry(Long outboxId, EventStatus eventStatus);

    int markFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount);

}
