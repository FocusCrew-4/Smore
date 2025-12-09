package com.smore.order.infrastructure.persistence.repository.order;

import com.smore.order.application.repository.OrderRepository;
import com.smore.order.domain.model.Order;
import com.smore.order.domain.status.OrderStatus;
import com.smore.order.infrastructure.error.OrderErrorCode;
import com.smore.order.infrastructure.persistence.entity.order.Address;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import com.smore.order.infrastructure.persistence.exception.CreateOrderFailException;
import com.smore.order.infrastructure.persistence.exception.NotFoundOrderException;
import com.smore.order.infrastructure.persistence.mapper.OrderMapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j(topic = "OrderRepositoryImpl")
@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order findByIdempotencyKey(UUID idempotencyKey) {

        if (idempotencyKey == null){
            log.error("idempotencyKey is Null : method = {}", "findByIdempotencyKey()");
            throw new IllegalArgumentException("idempotencyKey가 null입니다.");
        }

        OrderEntity entity = orderJpaRepository.findByIdempotencyKey(idempotencyKey);

        if (entity == null) return null;
        return OrderMapper.toDomain(entity);
    }

    @Override
    public Order findById(UUID orderId) {

        if (orderId == null) {
            log.error("orderId is Null : methodName = {}", "markComplete()");
            throw new IllegalArgumentException("주문 아이디가 null 입니다.");
        }

        OrderEntity entity = orderJpaRepository.findById(orderId)
            .orElseThrow(
                () -> new NotFoundOrderException(OrderErrorCode.NOT_FOUND_ORDER)
            );

        return OrderMapper.toDomain(entity);
    }

    @Override
    public Optional<Order> findByAllocationKeyAndUserId(UUID allocationKey, Long userId) {

        if (allocationKey == null) {
            log.error("allocationKey is Null : methodName = {}", "findByAllocationKeyAndUserId()");
            throw new IllegalArgumentException("allocationKey가 null 입니다.");
        }

        if (userId == null) {
            log.error("userId is Null : methodName = {}", "findByAllocationKeyAndUserId()");
            throw new IllegalArgumentException("userId가 null 입니다.");
        }

        OrderEntity entity = orderJpaRepository
            .findByAllocationKeyAndUserId(allocationKey, userId);

        return Optional.ofNullable(entity).map(OrderMapper::toDomain);
    }

    @Override
    public Optional<Order> findByIdIncludingDeleted(UUID orderId) {

        OrderEntity entity = orderJpaRepository.findByIdIncludingDeleted(orderId);

        return Optional.ofNullable(entity).map(OrderMapper::toDomain);
    }

    @Override
    public Page<Order> findAll(Long userId, UUID productId, Pageable pageable) {

        Page<OrderEntity> orderEntityPage = orderJpaRepository.findAll(userId, productId, pageable);

        return orderEntityPage.map(OrderMapper::toDomain);
    }

    @Override
    public Order save(Order order) {
        if (order == null){
            log.error("order is Null : methodName = {}", "save()");
            throw new IllegalArgumentException("order가 null 입니다.");
        }

        OrderEntity entity = orderJpaRepository.save(
            OrderMapper.toEntityForCreate(order)
        );

        if (entity == null) {
            log.error("entity is Null : methodName = {}", "save()");
            throw new CreateOrderFailException(OrderErrorCode.CREATE_ORDER_CONFLICT);
        }
        return OrderMapper.toDomain(entity);
    }

    @Override
    public int markComplete(UUID orderId) {

        if (orderId == null) {
            log.error("orderId is Null : methodName = {}", "markComplete()");
            throw new IllegalArgumentException("주문 아이디가 null 입니다.");
        }

        return orderJpaRepository.markComplete(orderId, OrderStatus.COMPLETED);
    }

    @Override
    public int updateRefundReservation(UUID orderId, Long userId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity,
        Collection<OrderStatus> statuses) {
        return orderJpaRepository.updateRefundReservation(
            orderId,
            userId,
            refundQuantity,
            refundReservedQuantity,
            refundedQuantity,
            statuses
        );
    }

    @Override
    public int applyRefundCompletion(UUID orderId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity, Integer refundAmount,
        OrderStatus status) {
        return orderJpaRepository.applyRefundCompletion(orderId, refundQuantity,
            refundReservedQuantity, refundedQuantity, refundAmount, status);
    }

    @Override
    public int refundFail(UUID orderId, Integer refundQuantity, Integer refundReservedQuantity,
        Integer refundedQuantity) {
        return orderJpaRepository.refundFail(orderId, refundQuantity, refundReservedQuantity,
            refundedQuantity);
    }

    @Override
    public int update(Order order) {

        Address newAddress = Address.of(
            order.getAddress().street(),
            order.getAddress().city(),
            order.getAddress().zipcode()
        );

        return orderJpaRepository.update(order.getId(), order.getUserId(), newAddress);
    }

    @Override
    public int delete(UUID orderId, Long userId, LocalDateTime now) {
        return orderJpaRepository.delete(orderId, userId, now);
    }

    @Override
    public int completePayment(UUID orderId, UUID paymentId) {
        return orderJpaRepository.completePayment(orderId, paymentId);
    }

    @Override
    public int fail(UUID orderId, OrderStatus currentStatus) {
        return orderJpaRepository.fail(orderId, currentStatus);
    }
}
