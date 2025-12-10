package com.smore.order.application.exception;

import com.smore.common.error.ErrorCode;
import com.smore.order.infrastructure.error.OrderException;

public class CompleteOrderFailException extends OrderException {

    public CompleteOrderFailException(ErrorCode errorCode) {
        super(errorCode);
    }

    @Override
    public ErrorCode getErrorCode() {
        return super.getErrorCode();
    }

    @Override
    public String getTriggeredBy() {
        return super.getTriggeredBy();
    }
}
