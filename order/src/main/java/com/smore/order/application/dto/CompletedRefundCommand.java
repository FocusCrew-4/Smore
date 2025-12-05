package com.smore.order.application.dto;

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
public class CompletedRefundCommand {
    private final UUID orderId;
    private final UUID refundId;
    private final Integer refundAmount;

    public static CompletedRefundCommand of(
        UUID orderId,
        UUID refundId,
        Integer refundAmount
    ) {
        return CompletedRefundCommand.builder()
            .orderId(orderId)
            .refundId(refundId)
            .refundAmount(refundAmount)
            .build();
    }
}
