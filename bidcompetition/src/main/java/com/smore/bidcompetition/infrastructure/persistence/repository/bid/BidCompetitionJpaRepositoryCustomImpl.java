package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import static com.smore.bidcompetition.infrastructure.persistence.entity.QBidCompetitionEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.bidcompetition.domain.status.BidStatus;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BidCompetitionJpaRepositoryCustomImpl implements BidCompetitionJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public BidCompetitionEntity findByIdempotencyKey(UUID idempotencyKey) {

        return queryFactory
            .select(bidCompetitionEntity)
            .from(bidCompetitionEntity)
            .where(
                bidCompetitionEntity.idempotencyKey.eq(idempotencyKey)
            )
            .fetchOne();
    }

    @Override
    public BidCompetitionEntity findByIdForUpdate(UUID bidId) {
        return queryFactory
            .select(bidCompetitionEntity)
            .from(bidCompetitionEntity)
            .where(
                bidCompetitionEntity.id.eq(bidId)
            )
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne();
    }

    @Override
    public int decreaseStock(UUID bidId, Integer quantity, LocalDateTime now) {

        long updated = queryFactory
            .update(bidCompetitionEntity)
            .set(bidCompetitionEntity.stock, bidCompetitionEntity.stock.subtract(quantity))
            .where(
                bidCompetitionEntity.id.eq(bidId),
                bidCompetitionEntity.stock.goe(quantity),
                bidCompetitionEntity.endAt.goe(now),
                bidCompetitionEntity.stock.gt(0),
                bidCompetitionEntity.bidStatus.eq(BidStatus.ACTIVE),
                bidCompetitionEntity.startAt.loe(now),
                bidCompetitionEntity.deletedAt.isNull()
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }
}
