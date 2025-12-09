package com.smore.order.infrastructure.persistence.entity.order;

import com.smore.order.domain.status.CancelState;
import com.smore.order.domain.status.OrderStatus;
import com.smore.order.domain.status.SaleType;
import com.smore.order.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 상품 카테고리, 주문 방식, SellerId(이벤트)
// RefundSucceded에도 Sellerid를 받아야 함 그래서 이벤트에 넣어야 함

@Getter
@Entity
@Table(
    name = "p_order",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_order_idempotency_key",
        columnNames = {"idempotency_key"}
    )
)
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Embedded
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_type", nullable = false)
    private SaleType saleType;

    @Column(name = "refund_reserved_quantity")
    private Integer refundReservedQuantity;

    @Column(name = "refunded_quantity")
    private Integer refundedQuantity;

    @Column(name = "refunded_amount")
    private Integer refundedAmount;

    @Column(name = "fee_amount")
    private Integer feeAmount;

    @Column(name = "idempotency_key", nullable = false)
    private UUID idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_status", nullable = false)
    private CancelState cancelState;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Embedded
    private Address address;

    public static OrderEntity create(
        Long userId,
        UUID productId, Integer productPrice, Integer quantity, Integer totalAmount,
        UUID categoryId, SaleType saleType,
        UUID idempotencyKey, OrderStatus orderStatus, CancelState cancelState,
        LocalDateTime orderedAt,
        String street, String city, String zipcode
    ) {
        Product product = new Product(productId, productPrice);
        Address address = new Address(street, city, zipcode);

        if (userId == null) throw new IllegalArgumentException("유저 식별자는 필수값입니다.");
        if (quantity == null)throw new IllegalArgumentException("주문 수량은 필수값입니다.");
        if (quantity < 1)throw new IllegalArgumentException("주문 수량은 1개 이상이어야 합니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("멱등키는 필수입니다.");
        if (orderStatus != OrderStatus.CREATED) throw new IllegalArgumentException("주문 생성 시 OrderStatus의 상태는 CREATED 상태여야 합니다.");
        if (cancelState != CancelState.NONE) throw new IllegalArgumentException("주문 생성 시 CancelStatus의 상태는 NONE 상태여야 합니다.");
        if (orderedAt == null) throw new IllegalArgumentException("현재 날짜는 필수입니다.");

        return OrderEntity.builder()
            .userId(userId)
            .product(product)
            .quantity(quantity)
            .totalAmount(totalAmount)
            .categoryId(categoryId)
            .saleType(saleType)
            .refundReservedQuantity(0)
            .refundedQuantity(0)
            .refundedAmount(0)
            .feeAmount(0)
            .idempotencyKey(idempotencyKey)
            .orderStatus(orderStatus)
            .cancelState(cancelState)
            .orderedAt(orderedAt)
            .address(address)
            .build();
    }
}
