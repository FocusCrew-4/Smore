package com.smore.bidcompetition.domain.status;

public enum EventStatus {

    PENDING("전송 대기중"),
    PROCESSING("처리 중"),
    SENT("전송 완료"),
    FAILED("영구 실패")
    ;

    private final String description;

    EventStatus(String description) {
        this.description = description;
    }
}
