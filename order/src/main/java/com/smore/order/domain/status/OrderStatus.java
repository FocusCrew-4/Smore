package com.smore.order.domain.status;

public enum OrderStatus {
    CREATED("주문 생성"),
    COMPLETED("주문 완료"),
    FAILED("주문 실패"),
    CANCELLED("주문 취소")
    ;

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
