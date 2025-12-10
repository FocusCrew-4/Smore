package com.smore.order.presentation.dto;

import com.smore.order.domain.model.Order;
import com.smore.order.domain.status.OrderStatus;
import com.smore.order.domain.vo.Address;
import com.smore.order.domain.vo.Product;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfo {

    private UUID orderId;
    private Long userId;
    private Product product;
    private Integer quantity;
    private Integer totalAmount;
    private Integer refundedAmount;
    private OrderStatus orderStatus;
    private LocalDateTime orderedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private Address address;

    public static OrderInfo of(Order order) {

        if (order == null) {
            return null;
        }

        Product product = order.getProduct();
        Address address = order.getAddress();

        return OrderInfo.builder()
            .orderId(order.getId())
            .userId(order.getUserId())
            .product(product)
            .quantity(order.getQuantity())
            .totalAmount(order.getTotalAmount())
            .refundedAmount(order.getRefundedAmount())
            .orderStatus(order.getOrderStatus())
            .orderedAt(order.getOrderedAt())
            .confirmedAt(order.getConfirmedAt())
            .cancelledAt(order.getCancelledAt())
            .address(address)
            .build();
    }
}
