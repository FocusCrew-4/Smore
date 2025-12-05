package com.smore.order.infrastructure.persistence.repository.outbox;

import com.smore.order.domain.status.EventStatus;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutboxJpaRepositoryCustom {

    Page<Long> findPendingIds(Collection<EventStatus> states, Pageable pageable);

    int claim(Long outboxId, EventStatus eventStatus);

    int markSent(Long outboxId, EventStatus eventStatus);

    int markRetry(Long outboxId, EventStatus eventStatus);

    int markFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount);

}
