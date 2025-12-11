package com.smore.payment.payment.infrastructure.persistence.jpa.repository.outbox;

import com.smore.payment.global.outbox.OutboxMessage;
import com.smore.payment.payment.domain.repository.OutboxRepository;
import com.smore.payment.payment.infrastructure.persistence.jpa.mapper.OutboxMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;
    private final OutboxMapper outboxMapper;

    @Override
    public void save(OutboxMessage outboxMessage) {
        outboxJpaRepository.save(outboxMapper.toEntity(outboxMessage));
    }
}
