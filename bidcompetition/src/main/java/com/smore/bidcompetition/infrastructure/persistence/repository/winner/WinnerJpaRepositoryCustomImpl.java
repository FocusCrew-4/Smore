package com.smore.bidcompetition.infrastructure.persistence.repository.winner;



import static com.smore.bidcompetition.infrastructure.persistence.entity.QWinnerEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.bidcompetition.domain.status.WinnerStatus;
import com.smore.bidcompetition.infrastructure.persistence.entity.WinnerEntity;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WinnerJpaRepositoryCustomImpl implements WinnerJpaRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public WinnerEntity findByIdempotencyKey(UUID idempotencyKey) {

        return queryFactory
            .select(winnerEntity)
            .from(winnerEntity)
            .where(
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
    public int markCancelled(UUID bidId, UUID allocationKey, Long version) {
        long updated = queryFactory
            .update(winnerEntity)
            .set(winnerEntity.winnerStatus, WinnerStatus.CANCELLED)
            .set(winnerEntity.version, winnerEntity.version.add(1))
            .where(
                winnerEntity.bidId.eq(bidId),
                winnerEntity.allocationKey.eq(allocationKey),
                winnerEntity.winnerStatus.eq(WinnerStatus.PAYMENT_PENDING),
                winnerEntity.version.eq(version)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }
}
