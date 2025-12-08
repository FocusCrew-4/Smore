package com.smore.order.presentation.dto;

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
public class RefundResponse {

    private final boolean refundAvailable;
    private final UUID orderId;
    private final UUID refundId;
    private final Integer refundQuantity;
    private final Integer refundAmount;
    private final RefundStatus status;
    private final Integer refundReservedQuantity;
    private final Integer refundedQuantity;
    private final LocalDateTime requestedAt;
    private final String message;

    public static RefundResponse success(
        UUID orderId,
        UUID refundId,
        Integer refundQuantity,
        Integer refundAmount,
        RefundStatus status,
        Integer refundReservedQuantity,
        Integer refundedQuantity,
        LocalDateTime requestedAt,
        String message
    ) {
        return RefundResponse.builder()
            .refundAvailable(true)
            .orderId(orderId)
            .refundId(refundId)
            .refundQuantity(refundQuantity)
            .refundAmount(refundAmount)
            .status(status)
            .refundReservedQuantity(refundReservedQuantity)
            .refundedQuantity(refundedQuantity)
            .requestedAt(requestedAt)
            .message(message)
            .build();
    }

    public static RefundResponse fail(
        UUID orderId,
        Integer refundQuantity,
        Integer refundAmount,
        Integer refundReservedQuantity,
        Integer refundedQuantity,
        String message
    ) {

        return RefundResponse.builder()
            .refundAvailable(false)
            .orderId(orderId)
            .refundId(null)
            .refundQuantity(refundQuantity)
            .refundAmount(refundAmount)
            .status(null)
            .refundReservedQuantity(refundReservedQuantity)
            .refundedQuantity(refundedQuantity)
            .requestedAt(null)
            .message(message)
            .build();
    }

    public static RefundResponse duplication(
        UUID orderId,
        String message
    ) {
        return RefundResponse.builder()
            .refundAvailable(false)
            .orderId(orderId)
            .message(message)
            .build();
    }
}