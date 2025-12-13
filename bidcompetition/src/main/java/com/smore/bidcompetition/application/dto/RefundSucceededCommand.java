package com.smore.bidcompetition.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundSucceededCommand {
    private final UUID orderId;
    private final UUID refundId;
    private final Long userId;
    private final Integer quantity;
    private final UUID allocationKey;
    private final Integer refundAmount;
    private final String orderStatus;
    private final LocalDateTime publishedAt;

    public static RefundSucceededCommand of(
        UUID orderId,
        UUID refundId,
        Long userId,
        Integer quantity,
        UUID allocationKey,
        Integer refundAmount,
        String orderStatus,
        LocalDateTime now
    ) {
        return RefundSucceededCommand.builder()
            .orderId(orderId)
            .refundId(refundId)
            .userId(userId)
            .quantity(quantity)
            .allocationKey(allocationKey)
            .refundAmount(refundAmount)
            .orderStatus(orderStatus)
            .publishedAt(now)
            .build();
    }

    public boolean isRefunded() {
        return this.orderStatus.equals("REFUNDED");
    }
}
