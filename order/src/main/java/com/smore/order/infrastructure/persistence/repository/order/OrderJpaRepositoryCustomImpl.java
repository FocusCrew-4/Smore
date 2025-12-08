package com.smore.order.infrastructure.persistence.repository.order;

import static com.smore.order.infrastructure.persistence.entity.order.QOrderEntity.*;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smore.order.application.event.outbound.OrderCompletedEvent;
import com.smore.order.domain.status.OrderStatus;
import com.smore.order.infrastructure.persistence.entity.order.Address;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

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

    public Page<OrderEntity> findAll(Long userId, UUID productId, Pageable pageable) {

        List<OrderSpecifier> orderSpecifiers = getOrderSpecifiers(pageable);

        List<OrderEntity> content = queryFactory
            .select(orderEntity)
            .from(orderEntity)
            .where(
                userIdEq(userId),
                productIdEq(productId)
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]))
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(orderEntity.count())
            .from(orderEntity)
            .where(
                userIdEq(userId),
                productIdEq(productId)
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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

    @Override
    public int update(UUID orderId, Long userId, Address address) {

        long updated = queryFactory
            .update(orderEntity)
            .set(orderEntity.address, address)
            .where(
                orderEntity.id.eq(orderId),
                orderEntity.userId.eq(userId)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int delete(UUID orderId, Long userId, LocalDateTime now) {
        long updated = queryFactory
            .update(orderEntity)
            .set(orderEntity.deletedBy, userId)
            .set(orderEntity.deletedAt, now)
            .where(
                orderEntity.id.eq(orderId),
                orderEntity.userId.eq(userId),
                orderEntity.orderStatus.eq(OrderStatus.CONFIRMED),
                orderEntity.deletedAt.isNull(),
                orderEntity.deletedBy.isNull()
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    @Override
    public int completePayment(UUID orderId, String paymentId) {
        long updated = queryFactory
            .update(orderEntity)
            .set(orderEntity.paymentId, paymentId)
            .set(orderEntity.orderStatus, OrderStatus.COMPLETED)
            .where(
                orderEntity.id.eq(orderId),
                orderEntity.orderStatus.eq(OrderStatus.CREATED)
            )
            .execute();

        em.flush();
        em.clear();

        return (int) updated;
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? orderEntity.userId.eq(userId) : null;
    }

    private BooleanExpression productIdEq(UUID productId) {
        return productId != null ? orderEntity.product.productId.eq(productId) : null;
    }

    private static List<OrderSpecifier> getOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        for (Sort.Order o : pageable.getSort()) {

            // isAscending는 오름차순이면 true, 내림차순이면 false 반환
            com.querydsl.core.types.Order direction = o.isAscending() ?
                com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC;

            String property = o.getProperty();

            PathBuilder<?> path = new PathBuilder<>(OrderEntity.class, orderEntity.getMetadata());

            ComparableExpression<?> expr =
                path.getComparable(o.getProperty(), Comparable.class);

            orderSpecifiers.add(new OrderSpecifier(direction, expr));
        }
        return orderSpecifiers;
    }
}
