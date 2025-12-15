package com.smore.auction.application.exception;

import com.smore.common.error.ErrorCode;

public enum AppErrorCode implements ErrorCode {
    BIDDER_NOT_FOUND("A101", "존재하지 않는 입찰자 입니다");

    private final String code;
    private final String message;
    private final String triggeredBy;

    AppErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
        this.triggeredBy = ErrorCode.super.triggeredBy();
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public String triggeredBy() {
        return triggeredBy;
    }
}
