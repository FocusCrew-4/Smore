package com.smore.order.domain.model;

import com.smore.order.domain.status.RefundStatus;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Refund {

    private UUID id;
    private UUID orderId;
    private Long userId;
    private UUID productId;
    private Integer refundQuantity;
    private Integer refundAmount;
    private UUID idempotencyKey;
    private String reason;
    private RefundStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    public static Refund create(
        UUID orderId,
        Long userId,
        UUID productId, Integer productPrice,
        Integer refundQuantity,
        UUID idempotencyKey,
        String reason,
        LocalDateTime now
    ) {

        if (orderId == null) throw new IllegalArgumentException("orderId는 필수값입니다.");
        if (productId == null) throw new IllegalArgumentException("productId는 필수값입니다.");
        if (productPrice == null) throw new IllegalArgumentException("상품 가격은 필수값입니다.");
        if (productPrice < 0) throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다.");
        if (refundQuantity == null) throw new IllegalArgumentException("환불 수량은 필수값입니다.");
        if (refundQuantity <= 0) throw new IllegalArgumentException("환불 수량은 1개 이상이어야 합니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("환불 요청의 멱등키는 필수값입니다.");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("환불 사유는 필수값입니다.");
        if (now == null) throw new IllegalArgumentException("환불 요청 시각(requestedAt)은 필수값입니다.");

        Integer refundAmount = calculateRefundAmount(productPrice, refundQuantity);

        return Refund.builder()
            .orderId(orderId)
            .userId(userId)
            .productId(productId)
            .refundQuantity(refundQuantity)
            .refundAmount(refundAmount)
            .idempotencyKey(idempotencyKey)
            .reason(reason)
            .status(RefundStatus.REQUESTED)
            .requestedAt(now)
            .build();
    }

    public static Refund of(
        UUID id,
        UUID orderId,
        Long userId,
        UUID productId,
        Integer refundQuantity,
        Integer refundAmount,
        UUID idempotencyKey,
        String reason,
        RefundStatus status,
        LocalDateTime requestedAt,
        LocalDateTime completedAt
    ) {

        return Refund.builder()
            .id(id)
            .orderId(orderId)
            .userId(userId)
            .productId(productId)
            .refundQuantity(refundQuantity)
            .refundAmount(refundAmount)
            .idempotencyKey(idempotencyKey)
            .reason(reason)
            .status(status)
            .requestedAt(requestedAt)
            .completedAt(completedAt)
            .build();
    }

    private static Integer calculateRefundAmount(Integer productPrice, Integer refundQuantity) {
        return productPrice * refundQuantity;
    }
}
