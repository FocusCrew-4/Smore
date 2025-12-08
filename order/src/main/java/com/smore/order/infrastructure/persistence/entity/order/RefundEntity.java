package com.smore.order.infrastructure.persistence.entity.order;

import com.smore.order.domain.status.RefundStatus;
import com.smore.order.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
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

@Getter
@Entity
@Table(
    name = "p_refund",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_refund_idempotency_key",
        columnNames = {"idempotency_key"}
    )
)
@Builder(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "refund_quantity", nullable = false, updatable = false)
    private Integer refundQuantity;

    @Column(name = "refund_amount", nullable = false, updatable = false)
    private Integer refundAmount;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private UUID idempotencyKey;

    @Column(name = "reason", nullable = false, updatable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", nullable = false)
    private RefundStatus status;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public static RefundEntity create(
        UUID orderId,
        Long userId,
        UUID productId,
        Integer refundQuantity, Integer refundAmount,
        UUID idempotencyKey,
        String reason,
        RefundStatus status,
        LocalDateTime requestedAt
    ) {
        if (orderId == null) throw new IllegalArgumentException("orderId는 필수값입니다.");
        if (productId == null) throw new IllegalArgumentException("productId는 필수값입니다.");
        if (refundQuantity == null) throw new IllegalArgumentException("환불 수량은 필수값입니다.");
        if (refundAmount == null) throw new IllegalArgumentException("환불 금액은 필수값입니다.");
        if (refundAmount < 0) throw new IllegalArgumentException("환불 금액은 0 이상이어야 합니다.");
        if (refundQuantity <= 0) throw new IllegalArgumentException("환불 수량은 1개 이상이어야 합니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("환불 요청의 멱등키는 필수값입니다.");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("환불 사유는 필수값입니다.");
        if (status == null) throw new IllegalArgumentException("환불 상태는 필수값입니다.");
        if (requestedAt == null) throw new IllegalArgumentException("환불 요청 시각(requestedAt)은 필수값입니다.");

        return RefundEntity.builder()
            .orderId(orderId)
            .userId(userId)
            .productId(productId)
            .refundQuantity(refundQuantity)
            .refundAmount(refundAmount)
            .idempotencyKey(idempotencyKey)
            .reason(reason)
            .status(status)
            .requestedAt(requestedAt)
            .build();
    }
}
