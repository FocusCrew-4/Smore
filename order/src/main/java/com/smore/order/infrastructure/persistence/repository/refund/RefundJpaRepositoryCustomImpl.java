package com.smore.order.infrastructure.persistence.repository.refund;

import static com.smore.order.infrastructure.persistence.entity.order.QRefundEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.order.domain.status.RefundStatus;
import com.smore.order.infrastructure.persistence.entity.order.RefundEntity;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RefundJpaRepositoryCustomImpl implements RefundJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public RefundEntity findByIdempotencyKey(UUID idempotencyKey) {
        return queryFactory
            .select(refundEntity)
            .from(refundEntity)
            .where(
                refundEntity.idempotencyKey.eq(idempotencyKey)
            )
            .fetchOne();
    }

    @Override
    public int complete(UUID refundId, RefundStatus status, LocalDateTime now) {
        long updated = queryFactory
            .update(refundEntity)
            .set(refundEntity.status, status)
            .set(refundEntity.completedAt, now)
            .where(
                refundEntity.id.eq(refundId),
                refundEntity.status.eq(RefundStatus.REQUESTED)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int fail(UUID id, RefundStatus refundStatus, String message, LocalDateTime now) {
        long updated = queryFactory
            .update(refundEntity)
            .set(refundEntity.status, refundStatus)
            .set(refundEntity.refundFailReason, message)
            .where(
                refundEntity.id.eq(id),
                refundEntity.status.eq(RefundStatus.REQUESTED)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }
}
