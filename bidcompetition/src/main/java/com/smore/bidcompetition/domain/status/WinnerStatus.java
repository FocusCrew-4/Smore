package com.smore.bidcompetition.domain.status;

import java.util.EnumSet;

public enum WinnerStatus {
    PAYMENT_PENDING("결제 대기"),
    PAID("결제 완료"),
    EXPIRED("만료"),
    CANCELLED("취소")
    ;

    public static final EnumSet<WinnerStatus> CANCELABLE_STATUSES =
        EnumSet.of(PAYMENT_PENDING, PAID);

    private final String description;

    WinnerStatus(String description) {
        this.description = description;
    }

}
