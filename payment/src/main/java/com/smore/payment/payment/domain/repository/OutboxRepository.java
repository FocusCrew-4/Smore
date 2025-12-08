package com.smore.payment.payment.domain.repository;

import com.smore.payment.global.outbox.OutboxMessage;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository {
    void save(OutboxMessage outboxMessage);
}
