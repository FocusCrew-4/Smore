package com.smore.bidcompetition.domain.status;

public enum WinnerStatus {
    PAYMENT_PENDING("결제 대기"),
    PAID("결제 완료"),
    EXPIRED("만료"),
    CANCELLED("취소")
    ;

    private final String description;

    WinnerStatus(String description) {
        this.description = description;
    }

}
