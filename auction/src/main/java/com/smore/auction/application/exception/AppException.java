package com.smore.auction.application.exception;


import com.smore.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String triggeredBy;

    public AppException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
        this.triggeredBy = errorCode.triggeredBy();
    }
}
