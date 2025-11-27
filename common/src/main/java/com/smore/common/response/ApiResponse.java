package com.smore.common.response;

import com.smore.common.error.ErrorCode;

public class ApiResponse {

    public static <T> CommonResponse<T> ok(T data) {
        return new CommonResponse<>(true, data, null);
    }

    public static CommonResponse<?> error(String code, String message) {
        return new CommonResponse<>(false, null, new ErrorResponse(code, message, null));
    }

    public static CommonResponse<?> error(ErrorCode errorCode) {
        return new CommonResponse<>(false, errorCode, new ErrorResponse(errorCode.code(),
            errorCode.message(), errorCode.triggeredBy()));
    }
}
