package com.smore.order.infrastructure.persistence.repository.outbox;

import com.smore.order.domain.status.EventStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutboxJpaRepositoryCustom {

    Page<Long> findPendingIds(Collection<EventStatus> states, Pageable pageable);

    List<Long> findExpiredProcessingIds(LocalDateTime expiredAt);

    int claim(Long outboxId, EventStatus eventStatus);

    int markSent(Long outboxId, EventStatus eventStatus);

    int markRetry(Long outboxId, EventStatus eventStatus);

    int markFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount);

    int bulkResetExpiredProcessingToReady(List<Long> ids);

}
