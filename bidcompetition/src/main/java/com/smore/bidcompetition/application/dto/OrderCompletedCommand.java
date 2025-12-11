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
public class OrderCompletedCommand {

    private final UUID orderId;
    private final Long userId;
    private final String currentOrderStatus;
    private final UUID allocationKey;
    private final LocalDateTime paidAt;

    public static OrderCompletedCommand of(
        UUID orderId, Long userId, String currentOrderStatus, UUID allocationKey,
        LocalDateTime paidAt
    ) {
        return OrderCompletedCommand.builder()
            .orderId(orderId)
            .userId(userId)
            .currentOrderStatus(currentOrderStatus)
            .allocationKey(allocationKey)
            .paidAt(paidAt)
            .build();
    }
}
