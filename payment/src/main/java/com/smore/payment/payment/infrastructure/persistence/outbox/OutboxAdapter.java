package com.smore.payment.payment.infrastructure.persistence.outbox;

import com.smore.payment.shared.outbox.OutboxMessage;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.infrastructure.persistence.jpa.mapper.OutboxMapper;
import com.smore.payment.payment.infrastructure.persistence.jpa.repository.outbox.OutboxJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxAdapter implements OutboxPort {

    private final OutboxJpaRepository outboxJpaRepository;
    private final OutboxMapper outboxMapper;

    @Override
    public void save(OutboxMessage outboxMessage) {
        outboxJpaRepository.save(outboxMapper.toEntity(outboxMessage));
    }
}
