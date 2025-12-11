package com.smore.order.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CompletedPaymentCommand {
    private UUID orderId;
    private UUID paymentId;
    private Integer totalAmount;
    private LocalDateTime approvedAt;

    public static CompletedPaymentCommand of(
        UUID orderId,
        UUID paymentId,
        Integer totalAmount,
        LocalDateTime approvedAt
    ) {
        return CompletedPaymentCommand.builder()
            .orderId(orderId)
            .paymentId(paymentId)
            .totalAmount(totalAmount)
            .approvedAt(approvedAt)
            .build();
    }
}
