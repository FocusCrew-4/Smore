package com.smore.order.presentation.dto;

import com.smore.order.domain.model.Order;
import com.smore.order.domain.status.OrderStatus;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IsOrderCreatedResponse {

    private final Boolean orderAvailable;
    private final UUID orderId;
    private final Integer totalAmount;
    private final OrderStatus orderStatus;
    private final UUID productId;
    private final Integer quantity;

    public static IsOrderCreatedResponse from(Order order) {
        return IsOrderCreatedResponse.builder()
            .orderAvailable(true)
            .orderId(order.getId())
            .totalAmount(order.getTotalAmount())
            .orderStatus(order.getOrderStatus())
            .productId(order.getProduct().productId())
            .quantity(order.getQuantity())
            .build();
    }

    public static IsOrderCreatedResponse notFoundOrder() {
        return IsOrderCreatedResponse.builder()
            .orderAvailable(false)
            .build();
    }

}
