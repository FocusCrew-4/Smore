package com.smore.order.infrastructure.persistence.repository.order;

import com.smore.order.domain.status.OrderStatus;
import com.smore.order.infrastructure.persistence.entity.order.Address;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderJpaRepositoryCustom {

    OrderEntity findByIdempotencyKey(UUID idempotencyKey);

    OrderEntity findByAllocationKeyAndUserId(UUID allocationKey, Long userId);

    Page<OrderEntity> findAll(Long userId, UUID productId, Pageable pageable);

    int markComplete(UUID orderId, OrderStatus status);

    int updateRefundReservation(UUID orderId, Long userId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity,
        Collection<OrderStatus> statuses);

    int applyRefundCompletion(UUID orderId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity, Integer refundAmount,
        OrderStatus status);

    int refundFail(UUID orderId, Integer refundQuantity, Integer refundReservedQuantity,
        Integer refundedQuantity);

    int update(UUID orderId, Long userId, Address address);

    int delete(UUID orderId, Long userId, LocalDateTime now);

    int completePayment(UUID orderId, UUID paymentId);
}
