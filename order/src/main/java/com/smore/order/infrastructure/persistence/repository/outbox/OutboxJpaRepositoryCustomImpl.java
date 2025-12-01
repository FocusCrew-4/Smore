package com.smore.order.infrastructure.persistence.repository.outbox;

import static com.smore.order.infrastructure.persistence.entity.outbox.QOutboxEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;

import com.smore.order.domain.status.EventStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class OutboxJpaRepositoryCustomImpl implements OutboxJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public int claim(Long outboxId, EventStatus eventStatus) {

        long updated = queryFactory
            .update(outboxEntity)
            .set(outboxEntity.eventStatus, eventStatus)
            .where(
                outboxEntity.id.eq(outboxId),
                outboxEntity.eventStatus.eq(EventStatus.PENDING)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int markSent(Long outboxId, EventStatus eventStatus) {
        long updated = queryFactory
            .update(outboxEntity)
            .set(outboxEntity.eventStatus, eventStatus)
            .where(
                outboxEntity.id.eq(outboxId),
                outboxEntity.eventStatus.eq(EventStatus.PROCESSING)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int makeRetry(Long outboxId, EventStatus eventStatus) {
        long updated = queryFactory
            .update(outboxEntity)
            .set(outboxEntity.eventStatus, eventStatus)
            .set(outboxEntity.retryCount, outboxEntity.retryCount.add(1))
            .where(
                outboxEntity.id.eq(outboxId),
                outboxEntity.eventStatus.eq(EventStatus.PROCESSING)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int makeFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount) {
        long updated = queryFactory
            .update(outboxEntity)
            .set(outboxEntity.eventStatus, eventStatus)
            .where(
                outboxEntity.id.eq(outboxId),
                outboxEntity.eventStatus.eq(EventStatus.PROCESSING),
                outboxEntity.retryCount.goe(maxRetryCount)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }
}
