package com.smore.payment.payment.infrastructure.persistence.repository.outbox;

import com.smore.payment.payment.infrastructure.persistence.model.outbox.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity,Long> {
}
