package com.smore.order.infrastructure.persistence.repository.outbox;

import com.smore.order.application.repository.OutboxRepository;
import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.EventStatus;
import com.smore.order.infrastructure.persistence.entity.outbox.OutboxEntity;
import com.smore.order.infrastructure.persistence.exception.CreateOutboxFailException;
import com.smore.order.infrastructure.persistence.exception.NotFoundOutboxException;
import com.smore.order.infrastructure.persistence.mapper.OutboxMapper;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public int claim(Long outboxId, EventStatus eventStatus) {

        if (outboxId == null) {
            log.error("outboxId is Null : method = {}", "claim()");
            throw new IllegalArgumentException("outboxId가 null입니다.");
        }

        return outboxJpaRepository.claim(outboxId, eventStatus);
    }

    @Transactional
    @Override
    public int markSent(Long outboxId, EventStatus eventStatus) {

        if (outboxId == null) {
            log.error("outboxId is Null : method = {}", "claim()");
            throw new IllegalArgumentException("outboxId가 null입니다.");
        }

        return outboxJpaRepository.markSent(outboxId, eventStatus);
    }

    @Transactional
    @Override
    public int makeRetry(Long outboxId, EventStatus eventStatus) {

        if (outboxId == null) {
            log.error("outboxId is Null : method = {}", "makeRetry()");
            throw new IllegalArgumentException("outboxId가 null입니다.");
        }

        return outboxJpaRepository.makeRetry(outboxId, eventStatus);
    }

    @Transactional
    @Override
    public int makeFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount) {

        if (outboxId == null) {
            log.error("outboxId is Null : method = {}", "claim()");
            throw new IllegalArgumentException("outboxId가 null입니다.");
        }

        if (maxRetryCount == null || maxRetryCount < 1) {
            log.error("maxRetryCount is Null : method = {}", "makeFail()");
            throw new IllegalArgumentException("maxRetryCount 값은 필수이며 양수여야 합니다.");
        }

        return outboxJpaRepository.makeFail(outboxId, eventStatus, maxRetryCount);
    }

    @Override
    public Page<Long> findPendingIds(Collection<EventStatus> states, Pageable pageable) {

        if (states == null || states.isEmpty()) {
            log.error("states가 유효하지 않습니다. : method = {}", "findPendingIds()");
            throw new IllegalArgumentException("states가 유효하지 않습니다.");
        }

        if (pageable == null) {
            log.error("pageable이 유효하지 않습니다. : method = {}", "findPendingIds()");
            throw new IllegalArgumentException("pageable이 유효하지 않습니다.");
        }

        return outboxJpaRepository.findPendingIds(states, pageable);
    }
}
