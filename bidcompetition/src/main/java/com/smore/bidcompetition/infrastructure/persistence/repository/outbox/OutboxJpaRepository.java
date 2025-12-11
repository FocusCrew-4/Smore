package com.smore.bidcompetition.infrastructure.persistence.repository.outbox;

import com.smore.bidcompetition.infrastructure.persistence.entity.OutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxJpaRepository extends JpaRepository<OutboxEntity, Long>,
    OutboxJpaRepositoryCustom {

}
