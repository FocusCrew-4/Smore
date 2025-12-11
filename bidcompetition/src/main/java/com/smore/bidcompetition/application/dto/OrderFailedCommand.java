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
public class OrderFailedCommand {
    private final UUID allocationKey;
    private final UUID productId;
    private final Long userID;
    private final Integer quantity;
    private final LocalDateTime publishedAt;

    public static OrderFailedCommand of(
        UUID allocationKey,
        UUID productId,
        Long userId,
        Integer quantity,
        LocalDateTime publishedAt
    ) {
        return OrderFailedCommand.builder()
            .allocationKey(allocationKey)
            .productId(productId)
            .userID(userId)
            .quantity(quantity)
            .publishedAt(publishedAt)
            .build();
    }

}
