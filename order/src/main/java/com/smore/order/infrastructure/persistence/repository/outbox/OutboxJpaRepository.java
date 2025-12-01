package com.smore.order.infrastructure.persistence.repository.outbox;

import com.smore.order.infrastructure.persistence.entity.outbox.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, Long>,
    OutboxJpaRepositoryCustom {

}
