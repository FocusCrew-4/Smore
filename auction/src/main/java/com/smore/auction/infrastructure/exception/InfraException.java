package com.smore.auction.infrastructure.exception;


import com.smore.common.error.ErrorCode;
import lombok.Getter;

@Getter
public class InfraException extends RuntimeException{

    private final ErrorCode errorCode;
    private final String triggeredBy;

    public InfraException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
        this.triggeredBy = errorCode.triggeredBy();
    }
}
