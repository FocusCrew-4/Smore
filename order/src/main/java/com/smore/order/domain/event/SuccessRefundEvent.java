package com.smore.order.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class SuccessRefundEvent implements OrderEvent {
    private final UUID orderId;
    private final Long userId;
    private final UUID allocationKey;
    private final Integer refundAmount;
    private final LocalDateTime publishedAt;

    public static SuccessRefundEvent of(
        UUID orderId, Long userId, UUID allocationKey, Integer refundAmount,
        LocalDateTime now) {

        return SuccessRefundEvent.builder()
            .orderId(orderId)
            .userId(userId)
            .allocationKey(allocationKey)
            .refundAmount(refundAmount)
            .publishedAt(now)
            .build();
    }
}
