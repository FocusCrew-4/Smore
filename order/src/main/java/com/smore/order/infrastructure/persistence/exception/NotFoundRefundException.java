package com.smore.order.infrastructure.persistence.exception;

import com.smore.common.error.ErrorCode;
import com.smore.order.infrastructure.error.OrderException;

public class NotFoundRefundException extends OrderException {

    public NotFoundRefundException(ErrorCode errorCode) {
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
