package com.smore.bidcompetition.application.dto;

public enum ServiceResult {
    SUCCESS("성공"),
    FAIL("실패")
    ;
    private String description;

    ServiceResult(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFailed() {
        return this == FAIL;
    }
}
