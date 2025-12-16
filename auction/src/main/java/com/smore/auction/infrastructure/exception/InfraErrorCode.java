package com.smore.auction.infrastructure.exception;

import com.smore.common.error.ErrorCode;

public enum InfraErrorCode implements ErrorCode {
    AUCTION_ALREADY_EXIST("A001", "이미 경매방이 존재합니다");

    private final String code;
    private final String message;
    private final String triggeredBy;

    InfraErrorCode(String code, String message) {
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
