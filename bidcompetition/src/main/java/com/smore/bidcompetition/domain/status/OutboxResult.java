package com.smore.bidcompetition.domain.status;

public enum OutboxResult {
    SUCCESS("메시지 발행 성공"),
    FAIL("메시지 발행 실패")
    ;

    private final String description;

    OutboxResult(String description) {
        this.description = description;
    }
}
