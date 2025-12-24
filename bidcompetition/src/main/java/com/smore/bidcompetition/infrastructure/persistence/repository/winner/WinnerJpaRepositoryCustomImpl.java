package com.smore.bidcompetition.infrastructure.persistence.repository.winner;



import static com.smore.bidcompetition.infrastructure.persistence.entity.QWinnerEntity.*;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.bidcompetition.domain.status.WinnerStatus;
import com.smore.bidcompetition.infrastructure.persistence.entity.WinnerEntity;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class WinnerJpaRepositoryCustomImpl implements WinnerJpaRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public WinnerEntity findByIdempotencyKey(UUID bidId, UUID idempotencyKey) {

        return queryFactory
            .select(winnerEntity)
            .from(winnerEntity)
            .where(
                winnerEntity.bidId.eq(bidId),
                winnerEntity.idempotencyKey.eq(idempotencyKey)
            )
            .fetchOne();
    }

    @Override
    public WinnerEntity findByAllocationKey(UUID allocationKey) {
        return queryFactory
            .select(winnerEntity)
            .from(winnerEntity)
            .where(
                winnerEntity.allocationKey.eq(allocationKey)
            )
            .fetchOne();
    }

    @Override
    public Page<UUID> findExpiredWinners(LocalDateTime now, long bufferTime, Pageable pageable) {

        LocalDateTime cutoff = now.minusSeconds(bufferTime);

        List<UUID> content = queryFactory
            .select(winnerEntity.id)
            .from(winnerEntity)
            .where(
                winnerEntity.expireAt.loe(cutoff),
                winnerEntity.winnerStatus.eq(WinnerStatus.PAYMENT_PENDING)
            )
            .orderBy(winnerEntity.expireAt.asc(), winnerEntity.id.asc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(winnerEntity.count())
            .from(winnerEntity)
            .where(
                winnerEntity.expireAt.loe(cutoff),
                winnerEntity.winnerStatus.eq(WinnerStatus.PAYMENT_PENDING)
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public int winnerPaid(UUID allocationKey, UUID orderId, Long version) {
        long updated = queryFactory
            .update(winnerEntity)
            .set(winnerEntity.winnerStatus, WinnerStatus.PAID)
            .set(winnerEntity.version, winnerEntity.version.add(1))
            .set(winnerEntity.orderId, orderId)
            .where(
                winnerEntity.allocationKey.eq(allocationKey),
                winnerEntity.winnerStatus.eq(WinnerStatus.PAYMENT_PENDING),
                winnerEntity.version.eq(version)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int markCancelled(UUID bidId, UUID allocationKey, Collection<WinnerStatus> statuses, Long version) {
        long updated = queryFactory
            .update(winnerEntity)
            .set(winnerEntity.winnerStatus, WinnerStatus.CANCELLED)
            .set(winnerEntity.version, winnerEntity.version.add(1))
            .where(
                winnerEntity.bidId.eq(bidId),
                winnerEntity.allocationKey.eq(allocationKey),
                winnerEntity.winnerStatus.in(statuses),
                winnerEntity.version.eq(version)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int markExpired(UUID winnerId, Long version) {
        long updated = queryFactory
            .update(winnerEntity)
            .set(winnerEntity.winnerStatus, WinnerStatus.EXPIRED)
            .set(winnerEntity.version, winnerEntity.version.add(1))
            .where(
                winnerEntity.id.eq(winnerId),
                winnerEntity.winnerStatus.eq(WinnerStatus.PAYMENT_PENDING),
                winnerEntity.version.eq(version)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }
}
