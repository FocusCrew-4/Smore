package com.smore.order.infrastructure.persistence.mapper;


import com.smore.order.domain.model.Order;
import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;

public final class OrderMapper {

    private OrderMapper() {

    }

    public static OrderEntity toEntityForCreate(Order order) {
        if (order == null) {
            return null;
        }

        return OrderEntity.create(
            order.getUserId(),
            order.getProduct().productId(),
            order.getProduct().productPrice(),
            order.getQuantity(),
            order.getTotalAmount(),
            order.getIdempotencyKey(),
            order.getOrderStatus(),
            order.getCancelState(),
            order.getOrderedAt(),
            order.getAddress().street(),
            order.getAddress().city(),
            order.getAddress().zipcode()
        );
    }

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }

        return Order.of(
            entity.getId(),
            entity.getUserId(),
            entity.getProduct().getProductId(),
            entity.getProduct().getProductPrice(),
            entity.getQuantity(),
            entity.getTotalAmount(),
            entity.getRefundAmount(),
            entity.getFeeAmount(),
            entity.getIdempotencyKey(),
            entity.getOrderStatus(),
            entity.getCancelState(),
            entity.getOrderedAt(),
            entity.getConfirmedAt(),
            entity.getCancelledAt(),
            entity.getAddress().getStreet(),
            entity.getAddress().getCity(),
            entity.getAddress().getZipcode()
        );
    }
}
