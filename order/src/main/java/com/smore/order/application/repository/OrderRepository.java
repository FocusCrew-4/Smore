package com.smore.order.application.repository;

import com.smore.order.domain.model.Order;
import com.smore.order.domain.status.OrderStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepository {

    Order findByIdempotencyKey(UUID idempotencyKey);

    Order findById(UUID orderId);

    Optional<Order> findByAllocationKeyAndUserId(UUID allocationKey, Long userId);

    Optional<Order> findByIdIncludingDeleted(UUID orderId);

    Order save(Order order);

    Page<Order> findAll(Long userId, UUID productId, Pageable pageable);

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

    int completePayment(UUID orderId, UUID paymentId);

    int fail(UUID orderId, OrderStatus currentStatus);
}
