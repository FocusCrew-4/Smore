package com.smore.order.application.event.outbound;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


// paymentId, Reason
@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderRefundFailedEvent implements OrderEvent {
    private UUID orderId;
    private UUID refundId;
    private Long userId;
    private String message;
    private LocalDateTime publishedAt;

    public static OrderRefundFailedEvent of(
        UUID orderId,
        UUID refundId,
        Long userId,
        String message,
        LocalDateTime now
    ) {
        return OrderRefundFailedEvent.builder()
            .orderId(orderId)
            .refundId(refundId)
            .userId(userId)
            .message(message)
            .publishedAt(now)
            .build();
    }

}
