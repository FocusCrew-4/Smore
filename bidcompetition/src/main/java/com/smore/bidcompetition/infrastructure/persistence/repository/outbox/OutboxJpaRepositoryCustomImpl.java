package com.smore.bidcompetition.infrastructure.persistence.repository.outbox;

import static com.smore.bidcompetition.infrastructure.persistence.entity.QOutboxEntity.*;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.bidcompetition.domain.status.EventStatus;
import com.smore.bidcompetition.infrastructure.persistence.entity.QOutboxEntity;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class OutboxJpaRepositoryCustomImpl implements OutboxJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Page<Long> findPendingIds(Collection<EventStatus> states, Pageable pageable) {

        List<Long> content = queryFactory
            .select(outboxEntity.id)
            .from(outboxEntity)
            .where(
                outboxEntity.eventStatus.in(states)
            )
            .orderBy(outboxEntity.createdAt.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(outboxEntity.count())
            .from(outboxEntity)
            .where(
                outboxEntity.eventStatus.in(states)
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public int claim(Long outboxId, EventStatus eventStatus) {

        long updated = queryFactory
            .update(outboxEntity)
            .set(outboxEntity.eventStatus, eventStatus)
            .set(outboxEntity.updatedAt, LocalDateTime.now())
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
            .set(outboxEntity.updatedAt, LocalDateTime.now())
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
    public int markRetry(Long outboxId, EventStatus eventStatus) {
        long updated = queryFactory
            .update(outboxEntity)
            .set(outboxEntity.eventStatus, eventStatus)
            .set(outboxEntity.retryCount, outboxEntity.retryCount.add(1))
            .set(outboxEntity.updatedAt, LocalDateTime.now())
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
    public int markFail(Long outboxId, EventStatus eventStatus, Integer maxRetryCount) {
        long updated = queryFactory
            .update(outboxEntity)
            .set(outboxEntity.eventStatus, eventStatus)
            .set(outboxEntity.updatedAt, LocalDateTime.now())
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
