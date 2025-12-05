package com.smore.order.application.repository;

import com.smore.order.domain.model.Order;
import com.smore.order.domain.status.OrderStatus;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order findByIdempotencyKey(UUID idempotencyKey);

    Order findById(UUID orderId);

    Optional<Order> findByAllocationKeyAndUserId(UUID allocationKey, Long userId);

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
}
