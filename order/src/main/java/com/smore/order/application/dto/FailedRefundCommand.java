package com.smore.order.application.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FailedRefundCommand {

    private UUID orderId;
    private UUID refundId;
    private Integer refundAmount;
    private String message;

    public static FailedRefundCommand of(
        UUID orderId,
        UUID refundId,
        Integer refundAmount,
        String message
    ) {

        return FailedRefundCommand.builder()
            .orderId(orderId)
            .refundId(refundId)
            .refundAmount(refundAmount)
            .message(message)
            .build();
    }

}
