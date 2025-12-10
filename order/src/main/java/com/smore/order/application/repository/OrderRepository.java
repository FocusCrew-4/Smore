package com.smore.order.application.repository;

import com.smore.order.domain.model.Order;
import com.smore.order.domain.status.OrderStatus;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.query.Param;

public interface OrderRepository {

    Order findByIdempotencyKey(UUID idempotencyKey);

    Order findById(UUID orderId);

    Optional<Order> findByAllocationKeyAndUserId(UUID allocationKey, Long userId);

    Optional<Order> findByIdIncludingDeleted(UUID orderId);

    Order save(Order order);

    int markComplete(UUID orderId);

    int updateRefundReservation(UUID orderId, Long userId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity,
        Collection<OrderStatus> statuses);

    int applyRefundCompletion(UUID orderId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity,
        Integer refundAmount, OrderStatus status);

    int refundFail(UUID orderId, Integer refundQuantity, Integer refundReservedQuantity,
        Integer refundedQuantity);

    int update(Order order);

    int delete(UUID orderId, Long userId, LocalDateTime now);
}
