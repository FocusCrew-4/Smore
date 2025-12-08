package com.smore.order.infrastructure.persistence.repository.refund;

import static com.smore.order.infrastructure.persistence.entity.order.QRefundEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.order.infrastructure.persistence.entity.order.RefundEntity;
import jakarta.persistence.EntityManager;
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
}
