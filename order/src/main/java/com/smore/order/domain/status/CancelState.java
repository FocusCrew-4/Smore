package com.smore.order.domain.status;

public enum CancelState {
    NONE("주문"),
    CANCEL_REQUESTED("주문 취소 요청"),
    CANCELLED("주문 취소 완료")
    ;

    private final String description;

    CancelState(String description) {
        this.description = description;
    }
}
