package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import static com.smore.bidcompetition.infrastructure.persistence.entity.QBidCompetitionEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.bidcompetition.domain.status.BidStatus;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
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
    public List<UUID> findBidsToActivate(LocalDateTime now) {

        return queryFactory
            .select(bidCompetitionEntity.id)
            .from(bidCompetitionEntity)
            .where(
                bidCompetitionEntity.bidStatus.eq(BidStatus.SCHEDULED),
                bidCompetitionEntity.startAt.loe(now),
                bidCompetitionEntity.deletedAt.isNull()
            )
            .fetch();
    }

    @Override
    public List<UUID> findBidsToClose(LocalDateTime endAt) {
        return queryFactory
            .select(bidCompetitionEntity.id)
            .from(bidCompetitionEntity)
            .where(
                bidCompetitionEntity.bidStatus.eq(BidStatus.ACTIVE),
                bidCompetitionEntity.endAt.loe(endAt),
                bidCompetitionEntity.deletedAt.isNull()
            )
            .fetch();
    }

    @Override
    public List<UUID> findBidsToEnd(LocalDateTime now, long closeGraceSeconds) {

        LocalDateTime cutoff = now.minusSeconds(closeGraceSeconds);

        return queryFactory
            .select(bidCompetitionEntity.id)
            .from(bidCompetitionEntity)
            .where(
                bidCompetitionEntity.bidStatus.eq(BidStatus.CLOSED),
                bidCompetitionEntity.endAt.loe(cutoff),
                bidCompetitionEntity.deletedAt.isNull()
            )
            .fetch();
    }

    @Override
    public int decreaseStock(UUID bidId, Integer quantity, LocalDateTime acceptedAt) {

        long updated = queryFactory
            .update(bidCompetitionEntity)
            .set(bidCompetitionEntity.stock, bidCompetitionEntity.stock.subtract(quantity))
            .where(
                bidCompetitionEntity.id.eq(bidId),
                bidCompetitionEntity.stock.goe(quantity),
                bidCompetitionEntity.endAt.goe(acceptedAt),
                bidCompetitionEntity.stock.gt(0),
                bidCompetitionEntity.bidStatus.in(BidStatus.ACTIVE, BidStatus.CLOSED),
                bidCompetitionEntity.startAt.loe(acceptedAt),
                bidCompetitionEntity.deletedAt.isNull()
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int increaseStock(UUID bidId, Integer quantity) {
        long updated = queryFactory
            .update(bidCompetitionEntity)
            .set(bidCompetitionEntity.stock, bidCompetitionEntity.stock.add(quantity))
            .where(
                bidCompetitionEntity.id.eq(bidId),
                bidCompetitionEntity.bidStatus.in(BidStatus.ACTIVE, BidStatus.CLOSED)
            )
            .execute();

        return (int) updated;
    }

    @Override
    public int bulkActivateByStartAt(List<UUID> ids, LocalDateTime now) {
        if (ids == null || ids.isEmpty()) return 0;

        long updated = queryFactory
            .update(bidCompetitionEntity)
            .set(bidCompetitionEntity.bidStatus, BidStatus.ACTIVE)
            .where(
                bidCompetitionEntity.id.in(ids),
                bidCompetitionEntity.bidStatus.eq(BidStatus.SCHEDULED),
                bidCompetitionEntity.startAt.loe(now),
                bidCompetitionEntity.deletedAt.isNull()
            )
            .execute();

        return (int) updated;
    }

    @Override
    public int bulkCloseByEndAt(List<UUID> ids, LocalDateTime now) {
        if (ids == null || ids.isEmpty()) return 0;

        long updated = queryFactory
            .update(bidCompetitionEntity)
            .set(bidCompetitionEntity.bidStatus, BidStatus.CLOSED)
            .where(
                bidCompetitionEntity.id.in(ids),
                bidCompetitionEntity.bidStatus.eq(BidStatus.ACTIVE),
                bidCompetitionEntity.endAt.loe(now),
                bidCompetitionEntity.deletedAt.isNull()
            )
            .execute();

        return (int) updated;
    }

    @Override
    public int bulkFinalizeByValidAt(List<UUID> ids, LocalDateTime now, long closeGraceSeconds) {
        if (ids == null || ids.isEmpty()) return 0;

        LocalDateTime cutoff = now.minusSeconds(closeGraceSeconds);

        long updated = queryFactory
            .update(bidCompetitionEntity)
            .set(bidCompetitionEntity.bidStatus, BidStatus.END)
            .where(
                bidCompetitionEntity.id.in(ids),
                bidCompetitionEntity.bidStatus.eq(BidStatus.CLOSED),
                bidCompetitionEntity.endAt.loe(cutoff),
                bidCompetitionEntity.deletedAt.isNull()
            )
            .execute();

        return (int) updated;
    }
}
