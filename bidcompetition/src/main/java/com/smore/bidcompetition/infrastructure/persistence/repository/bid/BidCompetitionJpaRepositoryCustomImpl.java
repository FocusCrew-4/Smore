package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import static com.smore.bidcompetition.infrastructure.persistence.entity.QBidCompetitionEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import jakarta.persistence.EntityManager;
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
}
