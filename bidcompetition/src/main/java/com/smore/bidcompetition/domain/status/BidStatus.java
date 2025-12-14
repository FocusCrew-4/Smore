package com.smore.bidcompetition.domain.status;

public enum BidStatus {
    SCHEDULED("시작 전"),
    ACTIVE("진행 중"),
    CLOSED("판매 종료"),
    CANCELLED("취소"),
    END("정산 종료")
    ;

    private final String description;

    BidStatus(String description) {
        this.description = description;
    }
}
