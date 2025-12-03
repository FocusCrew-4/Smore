package com.smore.order.infrastructure.persistence.repository.order;

import com.smore.order.application.repository.OrderRepository;
import com.smore.order.domain.model.Order;
import com.smore.order.domain.status.OrderStatus;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import com.smore.order.infrastructure.persistence.exception.CreateOrderFailException;
import com.smore.order.infrastructure.persistence.exception.NotFoundOrderException;
import com.smore.order.infrastructure.persistence.mapper.OrderMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
            throw new CreateOrderFailException("주문이 생성되지 않았습니다.");
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
    public Order findById(UUID orderId) {

        if (orderId == null) {
            log.error("orderId is Null : methodName = {}", "markComplete()");
            throw new IllegalArgumentException("주문 아이디가 null 입니다.");
        }

        OrderEntity entity = orderJpaRepository.findById(orderId)
            .orElseThrow(
                () -> new NotFoundOrderException("주문을 찾을 수 없습니다.")
            );

        return OrderMapper.toDomain(entity);
    }
}
