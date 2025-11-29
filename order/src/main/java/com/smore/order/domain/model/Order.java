package com.smore.order.domain.model;


import com.smore.order.domain.status.CancelState;
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
public class Order {

    private UUID id;
    private Long userId;
    private Product product;
    private Integer quantity;
    private Integer totalAmount;
    private Integer refundAmount;
    private Integer feeAmount;
    private UUID idempotencyKey;
    private OrderStatus orderStatus;
    private CancelState cancelState;
    private LocalDateTime orderedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private Address address;

    public static Order create(
        Long userId, UUID productId,
        Integer productPrice, Integer quantity,
        UUID idempotencyKey, LocalDateTime now,
        String street, String city, String zipcode
    ) {

        Product product = new Product(productId, productPrice);
        Address address = new Address(street, city, zipcode);

        if (userId == null) throw new IllegalArgumentException("유저 식별자는 필수값입니다.");
        if (quantity == null)throw new IllegalArgumentException("주문 수량은 필수값입니다.");
        if (quantity < 1)throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("멱등키는 필수입니다.");
        if (now == null) throw new IllegalArgumentException("현재 날짜는 필수입니다.");

        Integer totalAmount = calculateTotalPrice(productPrice, quantity);

        return Order.builder()
            .userId(userId)
            .product(product)
            .quantity(quantity)
            .totalAmount(totalAmount)
            .idempotencyKey(idempotencyKey)
            .orderStatus(OrderStatus.CRATED)
            .cancelState(CancelState.NONE)
            .orderedAt(now)
            .address(address)
            .build();
    }

    private static Integer calculateTotalPrice(Integer price, Integer quantity) {
        return price * quantity;
    }
}
