package com.smore.order.infrastructure.persistence.repository.order;

import com.smore.order.domain.status.OrderStatus;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import java.util.Collection;
import java.util.UUID;

public interface OrderJpaRepositoryCustom {

    OrderEntity findByIdempotencyKey(UUID idempotencyKey);

    int markComplete(UUID orderId, OrderStatus status);

    OrderEntity findByAllocationKeyAndUserId(UUID allocationKey, Long userId);

    int updateRefundReservation(UUID orderId, Long userId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity,
        Collection<OrderStatus> statuses);

    int applyRefundCompletion(UUID orderId, Integer refundQuantity,
        Integer refundReservedQuantity, Integer refundedQuantity, Integer refundAmount,
        OrderStatus status);

    int refundFail(UUID orderId, Integer refundQuantity, Integer refundReservedQuantity,
        Integer refundedQuantity);
}
