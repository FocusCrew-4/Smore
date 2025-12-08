package com.smore.order.application.dto;

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
    private String paymentId;
    private Integer totalAmount;

    public static CompletedPaymentCommand of(
        UUID orderId,
        String paymentId,
        Integer totalAmount
    ) {
        return CompletedPaymentCommand.builder()
            .orderId(orderId)
            .paymentId(paymentId)
            .totalAmount(totalAmount)
            .build();
    }
}
