package com.smore.common.exception;

import com.smore.common.error.ErrorCode;

public class CommonException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String triggeredBy;

    public CommonException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
        this.triggeredBy = errorCode.triggeredBy();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public String getTriggeredBy() {
        return triggeredBy;
    }
}