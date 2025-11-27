package com.smore.common.response;

import java.time.Instant;

public record CommonResponse<T>(
    boolean success,
    T data,
    ErrorResponse error,
    Instant dateTime
) {
    CommonResponse(
        boolean success,
        T data,
        ErrorResponse error
    ) {
        this(success, data, error, Instant.now());
    }
}