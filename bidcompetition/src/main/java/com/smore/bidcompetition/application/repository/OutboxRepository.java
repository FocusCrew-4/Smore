package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.status.EventStatus;
import com.smore.bidcompetition.domain.model.Outbox;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OutboxRepository {

    Outbox findById(Long outboxId);

    Page<Long> findPendingIds(Collection<EventStatus> states, Pageable pageable);

    List<Long> findExpiredProcessingIds(LocalDateTime expiredAt);

    Outbox save(Outbox outbox);

    int claim(Long outboxId, EventStatus eventStatus);

    int markSent(Long outboxId, EventStatus eventStatus);

    int markRetry(Long outboxId, EventStatus eventStatus);

    int markFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount);

    int bulkResetExpiredProcessingToReady(List<Long> ids);

}
