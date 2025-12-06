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
    private Integer refundReservedQuantity;
    private Integer refundedQuantity;
    private Integer refundedAmount;
    private Integer feeAmount;
    private UUID idempotencyKey;
    private OrderStatus orderStatus;
    private CancelState cancelState;
    private LocalDateTime orderedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private Address address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

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
            .orderStatus(OrderStatus.CREATED)
            .cancelState(CancelState.NONE)
            .orderedAt(now)
            .address(address)
            .build();
    }

    public static Order of(
        UUID id,
        Long userId,
        UUID productId,
        Integer productPrice,
        Integer quantity,
        Integer totalAmount,
        Integer refundReservedQuantity,
        Integer refundedQuantity,
        Integer refundedAmount,
        Integer feeAmount,
        UUID idempotencyKey,
        OrderStatus orderStatus,
        CancelState cancelState,
        LocalDateTime orderedAt,
        LocalDateTime confirmedAt,
        LocalDateTime cancelledAt,
        String street,
        String city,
        String zipcode,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt,
        Long deletedBy
    ) {

        Product product = new Product(productId, productPrice);
        Address address = new Address(street, city, zipcode);

        return Order.builder()
            .id(id)
            .userId(userId)
            .product(product)
            .quantity(quantity)
            .totalAmount(totalAmount)
            .refundReservedQuantity(refundReservedQuantity)
            .refundedQuantity(refundedQuantity)
            .refundedAmount(refundedAmount)
            .feeAmount(feeAmount)
            .idempotencyKey(idempotencyKey)
            .orderStatus(orderStatus)
            .cancelState(cancelState)
            .orderedAt(orderedAt)
            .confirmedAt(confirmedAt)
            .cancelledAt(cancelledAt)
            .address(address)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .build();
    }

    public boolean isCompleted() {
        return orderStatus == OrderStatus.COMPLETED;
    }

    public boolean noRefund() {

        boolean noRemainingQuantity =
            quantity <= (refundedQuantity + refundReservedQuantity);

        return noRemainingQuantity || !orderStatus.isRefundable();
    }

    public OrderStatus calculateStatusAfterRefund(Integer additionalRefundQuantity) {
        if (additionalRefundQuantity <= 0) {
            throw new IllegalArgumentException("환불 수량은 1개 이상이어야 합니다.");
        }

        Integer newRefunded = refundedQuantity + additionalRefundQuantity;
        if (newRefunded > this.quantity) {
            throw new IllegalStateException("전체 주문 수량을 초과하여 환불할 수 없습니다.");
        }

        if (newRefunded == quantity) {
            return OrderStatus.REFUNDED;
        }

        return OrderStatus.PARTIALLY_REFUNDED;
    }

    public void changeAddressInfo(String street, String city, String zipcode) {
        Address address = new Address(street, city, zipcode);
    }

    public boolean notEqualUserId(Long userId) {
        return !this.userId.equals(userId);
    }

    public boolean equalAddress(Address address) {
        return this.address.equals(address);
    }

    private static Integer calculateTotalPrice(Integer price, Integer quantity) {
        return price * quantity;
    }
}
