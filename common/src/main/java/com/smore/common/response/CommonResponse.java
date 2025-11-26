package com.smore.common.response;

import java.time.LocalDateTime;

public record CommonResponse<T>(
    boolean success,
    T data,
    ErrorResponse error,
    LocalDateTime dateTime
) {
    CommonResponse(
        boolean success,
        T data,
        ErrorResponse error
    ) {
        this(success, data, error, LocalDateTime.now());
    }
}