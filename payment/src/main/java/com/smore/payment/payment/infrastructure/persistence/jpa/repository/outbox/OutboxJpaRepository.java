package com.smore.payment.payment.infrastructure.persistence.jpa.repository.outbox;

import com.smore.payment.shared.outbox.OutboxStatus;
import com.smore.payment.payment.infrastructure.persistence.jpa.model.outbox.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity,Long> {
    List<OutboxEntity> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
