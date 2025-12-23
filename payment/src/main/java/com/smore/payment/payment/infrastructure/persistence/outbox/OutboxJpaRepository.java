package com.smore.payment.payment.infrastructure.persistence.outbox;

import com.smore.payment.shared.outbox.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity,Long> {
    List<OutboxEntity> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);

    List<OutboxEntity> findTop50ByStatusAndRetryCountGreaterThanOrderByCreatedAtAsc(OutboxStatus status, Integer retryCount);
}
