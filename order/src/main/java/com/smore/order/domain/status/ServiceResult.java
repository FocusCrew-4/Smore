package com.smore.order.domain.status;

public enum ServiceResult {
    SUCCESS("서비스 작업 완료"),
    FAIL("서비스 작업 실패")
    ;

    private final String description;

    ServiceResult(String description) {
        this.description = description;
    }
}
