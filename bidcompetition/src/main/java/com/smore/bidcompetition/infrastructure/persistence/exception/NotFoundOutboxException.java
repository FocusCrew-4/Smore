package com.smore.bidcompetition.infrastructure.persistence.exception;

import com.smore.bidcompetition.infrastructure.error.BidException;
import com.smore.common.error.ErrorCode;


public class NotFoundOutboxException extends BidException {

    public NotFoundOutboxException(ErrorCode errorCode) {
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
