package com.smore.order.presentation.dto;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteOrderResponse {

    private final Boolean success;
    private final String message;
    private String deletedAt;
    private Long deletedBy;

    public static DeleteOrderResponse success(
        String message,
        LocalDateTime deletedAt,
        Long deletedBy
    ) {
        return DeleteOrderResponse.builder()
            .success(true)
            .message(message)
            .deletedAt(String.valueOf(deletedAt))
            .deletedBy(deletedBy)
            .build();
    }

    public static DeleteOrderResponse fail(
        String message
    ) {
        return DeleteOrderResponse.builder()
            .success(false)
            .message(message)
            .build();
    }
}
