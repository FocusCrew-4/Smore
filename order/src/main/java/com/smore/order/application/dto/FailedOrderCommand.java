package com.smore.order.application.dto;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class FailedOrderCommand {
    private final UUID orderId;
    private final UUID paymentId;
    private final String errorMessage;

    public static FailedOrderCommand of(UUID orderId, UUID paymentId, String errorMessage) {
        return FailedOrderCommand.builder()
            .orderId(orderId)
            .paymentId(paymentId)
            .errorMessage(errorMessage)
            .build();
    }
}
