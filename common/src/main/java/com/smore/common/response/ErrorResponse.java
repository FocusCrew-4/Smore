package com.smore.common.response;

public record ErrorResponse(
    String code,
    String message,
    String triggeredBy
) {
}
