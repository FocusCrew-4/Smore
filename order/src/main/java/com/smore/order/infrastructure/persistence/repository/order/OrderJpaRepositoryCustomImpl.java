package com.smore.order.infrastructure.persistence.repository.order;

import static com.smore.order.infrastructure.persistence.entity.order.QOrderEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import jakarta.persistence.EntityManager;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
public class OrderJpaRepositoryCustomImpl implements OrderJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public OrderEntity findByIdempotencyKey(UUID idempotencyKey) {
        return queryFactory
            .select(orderEntity)
            .from(orderEntity)
            .where(
                orderEntity.idempotencyKey.eq(idempotencyKey)
            )
            .fetchOne();
    }
}
