package com.smore.order.application.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundCommand {

    private final UUID orderId;
    private final Long userId;
    private final Integer refundQuantity;
    private final String reason;
    private final UUID idempotencyKey;

    public static RefundCommand of(
        UUID orderId,
        Long userId,
        Integer refundQuantity,
        String reason,
        UUID idempotencyKey
    ) {
        if (orderId == null) throw new IllegalArgumentException("orderId는 필수값입니다.");
        if (userId == null) throw new IllegalArgumentException("userId는 필수값입니다.");
        if (refundQuantity == null || refundQuantity <= 0) throw new IllegalArgumentException("환불 수량은 1개 이상이어야 합니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("멱등키는 필수값입니다.");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("환불 사유는 필수값입니다.");

        return RefundCommand.builder()
            .orderId(orderId)
            .userId(userId)
            .refundQuantity(refundQuantity)
            .reason(reason)
            .idempotencyKey(idempotencyKey)
            .build();
    }

    public Integer calculateRefundAmount(Integer productPrice) {
        if (productPrice == null || productPrice < 0) throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다.");
        return productPrice * refundQuantity;
    }

}
