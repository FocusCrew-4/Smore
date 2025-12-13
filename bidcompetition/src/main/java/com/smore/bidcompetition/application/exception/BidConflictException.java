package com.smore.bidcompetition.application.exception;

import com.smore.bidcompetition.infrastructure.error.BidException;
import com.smore.common.error.ErrorCode;

public class BidConflictException extends BidException {

    public BidConflictException(ErrorCode errorCode) {
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
