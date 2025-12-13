package com.smore.bidcompetition.infrastructure.persistence.repository.inventorylog;

import static com.smore.bidcompetition.infrastructure.persistence.entity.QBidInventoryLogEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidInventoryLogEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BidInventoryLogJpaRepositoryCustomImpl implements BidInventoryLogJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public BidInventoryLogEntity findByIdempotencyKey(String idempotencyKey) {

        return queryFactory
            .select(bidInventoryLogEntity)
            .from(bidInventoryLogEntity)
            .where(
                bidInventoryLogEntity.idempotencyKey.eq(idempotencyKey)
            )
            .fetchOne();
    }
}
