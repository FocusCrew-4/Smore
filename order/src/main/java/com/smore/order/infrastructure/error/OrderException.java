package com.smore.order.infrastructure.error;

import com.smore.common.error.ErrorCode;

public class OrderException  extends RuntimeException {

    private final ErrorCode errorCode;
    private final String triggeredBy;

    public OrderException(ErrorCode errorCode) {
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