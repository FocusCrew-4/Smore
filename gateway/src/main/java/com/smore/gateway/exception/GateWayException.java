package com.smore.gateway.exception;


import com.smore.common.error.ErrorCode;

public enum GateWayException implements ErrorCode {
    INVALID_TOKEN("G001", "Invalid JWT token"),
    UNAUTHORIZED("G002", "Unauthorized request");

    private final String code;
    private final String message;
    private final String triggeredBy;

    GateWayException(String code, String message) {
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
