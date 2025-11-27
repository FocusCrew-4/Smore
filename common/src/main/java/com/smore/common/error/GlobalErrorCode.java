package com.smore.common.error;

public enum GlobalErrorCode implements ErrorCode {
    INVALID_INPUT("C001", "Invalid input"),
    INTERNAL_ERROR("C002", "Internal server error");

    private final String code;
    private final String message;
    private final String triggeredBy;

    GlobalErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
        this.triggeredBy = TriggerLocator.resolve();
    }

    @Override
    public String code() { return code; }

    @Override
    public String message() { return message; }

    public String triggeredBy() { return triggeredBy; }
}
