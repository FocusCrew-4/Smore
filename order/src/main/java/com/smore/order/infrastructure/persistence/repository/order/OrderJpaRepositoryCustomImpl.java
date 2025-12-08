package com.smore.order.infrastructure.persistence.repository.order;

import static com.smore.order.infrastructure.persistence.entity.order.QOrderEntity.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.order.domain.status.OrderStatus;
import com.smore.order.domain.status.RefundStatus;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import jakarta.persistence.EntityManager;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

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

    @Override
    public OrderEntity findByAllocationKeyAndUserId(UUID allocationKey, Long userId) {
        return queryFactory
            .select(orderEntity)
            .from(orderEntity)
            .where(
                orderEntity.idempotencyKey.eq(allocationKey),
                orderEntity.userId.eq(userId)
            )
            .fetchOne();
    }

    @Override
    public int markComplete(UUID orderId, OrderStatus status) {
        long updated = queryFactory
            .update(orderEntity)
            .set(orderEntity.orderStatus, status)
            .where(
                orderEntity.id.eq(orderId),
                orderEntity.orderStatus.eq(OrderStatus.CREATED)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int updateRefundReservation(UUID orderId, Long userId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity,
        Collection<OrderStatus> statuses) {

        long updated = queryFactory
            .update(orderEntity)
            .set(orderEntity.refundReservedQuantity,
                orderEntity.refundReservedQuantity.add(refundQuantity))
            .where(
                orderEntity.id.eq(orderId),
                orderEntity.userId.eq(userId),
                orderEntity.refundReservedQuantity.eq(refundReservedQuantity),
                orderEntity.refundedQuantity.eq(refundedQuantity),
                orderEntity.orderStatus.in(statuses),
                orderEntity.quantity.goe(
                    orderEntity.refundReservedQuantity
                        .add(orderEntity.refundedQuantity)
                        .add(refundQuantity)
                )
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int applyRefundCompletion(UUID orderId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity, Integer refundAmount,
        OrderStatus status) {

        long updated = queryFactory
            .update(orderEntity)
            .set(orderEntity.refundReservedQuantity,
                orderEntity.refundReservedQuantity.subtract(refundQuantity))
            .set(orderEntity.refundedQuantity,
                orderEntity.refundedQuantity.add(refundQuantity))
            .set(orderEntity.refundedAmount,
                orderEntity.refundedAmount.add(refundAmount))
            .set(orderEntity.orderStatus, status)
            .where(
                orderEntity.id.eq(orderId),
                orderEntity.refundReservedQuantity.eq(refundReservedQuantity),
                orderEntity.refundedQuantity.eq(refundedQuantity)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int refundFail(UUID orderId, Integer refundQuantity, Integer refundReservedQuantity,
        Integer refundedQuantity) {

        long updated = queryFactory
            .update(orderEntity)
            .set(orderEntity.refundReservedQuantity,
                orderEntity.refundReservedQuantity.subtract(refundQuantity))
            .where(
                orderEntity.id.eq(orderId),
                orderEntity.refundedQuantity.eq(refundedQuantity),
                orderEntity.refundReservedQuantity.eq(refundReservedQuantity)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }
}
