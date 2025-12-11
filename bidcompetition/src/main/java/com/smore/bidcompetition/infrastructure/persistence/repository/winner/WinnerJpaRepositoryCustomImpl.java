package com.smore.bidcompetition.infrastructure.persistence.repository.winner;



import static com.smore.bidcompetition.infrastructure.persistence.entity.QWinnerEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.bidcompetition.infrastructure.persistence.entity.QWinnerEntity;
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
}
