package com.smore.order.infrastructure.persistence.repository.outbox;

import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.domain.model.Outbox;
import com.smore.order.infrastructure.persistence.entity.outbox.OutboxEntity;
import com.smore.order.infrastructure.persistence.exception.CreateOutboxFailException;
import com.smore.order.infrastructure.persistence.exception.NotFoundOutboxException;
import com.smore.order.infrastructure.persistence.mapper.OutboxMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j(topic = "OutboxRepositoryImpl")
@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

    private final OutboxJpaRepository outboxJpaRepository;

    @Override
    public Outbox save(Outbox outbox) {

        if (outbox == null) {
            log.error("outbox is Null : method = {}", "save()");
            throw new IllegalArgumentException("outbox null입니다.");
        }

         OutboxEntity entity = outboxJpaRepository.save(
            OutboxMapper.toEntityForCreate(outbox));

        if (entity == null) {
            log.error("entity is Null : method = {}", "save()");
            throw new CreateOutboxFailException("이벤트를 outbox에 저장하지 못했습니다.");
        }
        return OutboxMapper.toDomain(entity);
    }

    @Override
    public Outbox findById(Long outboxId) {

        if (outboxId == null) {
            log.error("outboxId is Null : method = {}", "findById()");
            throw new IllegalArgumentException("outboxId가 null입니다.");
        }

        OutboxEntity entity = outboxJpaRepository.findById(outboxId).orElseThrow(
            () -> new NotFoundOutboxException("outbox를 찾을 수 없습니다.")
        );

        return OutboxMapper.toDomain(entity);
    }

}
