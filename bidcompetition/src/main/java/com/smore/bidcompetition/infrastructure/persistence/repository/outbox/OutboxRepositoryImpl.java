package com.smore.bidcompetition.infrastructure.persistence.repository.outbox;


import com.smore.bidcompetition.application.repository.OutboxRepository;
import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.domain.status.EventStatus;
import com.smore.bidcompetition.infrastructure.error.BidErrorCode;
import com.smore.bidcompetition.infrastructure.persistence.entity.OutboxEntity;
import com.smore.bidcompetition.infrastructure.persistence.exception.CreateOutboxFailException;
import com.smore.bidcompetition.infrastructure.persistence.exception.NotFoundOutboxException;
import com.smore.bidcompetition.infrastructure.persistence.mapper.OutboxMapper;
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
    public Outbox findById(Long outboxId) {

        if (outboxId == null) {
            log.error("outboxId is Null : method = {}", "findById()");
            throw new IllegalArgumentException("outboxId가 null입니다.");
        }

        OutboxEntity entity = outboxJpaRepository.findById(outboxId).orElseThrow(
            () -> new NotFoundOutboxException(BidErrorCode.NOT_FOUND_OUTBOX)
        );

        return OutboxMapper.toDomain(entity);
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
            throw new CreateOutboxFailException(BidErrorCode.CREATE_OUTBOX_CONFLICT);
        }
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
    public int markRetry(Long outboxId, EventStatus eventStatus) {

        if (outboxId == null) {
            log.error("outboxId is Null : method = {}", "makeRetry()");
            throw new IllegalArgumentException("outboxId가 null입니다.");
        }

        return outboxJpaRepository.markRetry(outboxId, eventStatus);
    }

    @Transactional
    @Override
    public int markFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount) {

        if (outboxId == null) {
            log.error("outboxId is Null : method = {}", "claim()");
            throw new IllegalArgumentException("outboxId가 null입니다.");
        }

        if (maxRetryCount == null || maxRetryCount < 1) {
            log.error("maxRetryCount is Null : method = {}", "makeFail()");
            throw new IllegalArgumentException("maxRetryCount 값은 필수이며 양수여야 합니다.");
        }

        return outboxJpaRepository.markFail(outboxId, eventStatus, maxRetryCount);
    }
}
