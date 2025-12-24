package com.smore.bidcompetition.presentation.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BidResponse {
    private UUID bidId;
    private Integer quantity;
    private UUID allocationKey;
    private String expireAt;
    private String message;

    public static BidResponse success(
        UUID bidId,
        Integer quantity,
        UUID allocationKey,
        LocalDateTime expireAt
    ) {
        return BidResponse.builder()
            .bidId(bidId)
            .quantity(quantity)
            .allocationKey(allocationKey)
            .expireAt(expireAt.toString())
            .message("경쟁에서 승리하여 주문을 요청했습니다.")
            .build();
    }

    public static BidResponse fail(
        UUID bidId,
        Integer quantity,
        String message
    ) {
        return BidResponse.builder()
            .bidId(bidId)
            .quantity(quantity)
            .message(message)
            .build();
    }

    public static BidResponse processing(
        UUID bidId,
        Integer quantity,
        UUID allocationKey,
        String message
    ) {
        return BidResponse.builder()
            .bidId(bidId)
            .quantity(quantity)
            .allocationKey(allocationKey)
            .message(message)
            .build();
    }
}
