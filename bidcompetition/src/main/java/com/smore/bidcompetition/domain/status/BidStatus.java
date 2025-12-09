package com.smore.bidcompetition.domain.status;

public enum BidStatus {
    SCHEDULED("시작 전"),
    ACTIVE("진행 중"),
    CLOSED("종료 됨")
    ;

    private final String description;

    BidStatus(String description) {
        this.description = description;
    }
}
