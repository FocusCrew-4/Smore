package com.smore.order.domain.status;

import java.util.Set;

public enum OrderStatus {
    CREATED("주문 생성"),
    COMPLETED("주문 완료"),
    FAILED("주문 실패"),
    PARTIALLY_REFUNDED("부분 환불"),
    REFUNDED("전체 환불"),
    CANCELLED("주문 취소"),
    ;

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public static final Set<OrderStatus> REFUNDABLE_STATES =
        Set.of(COMPLETED, PARTIALLY_REFUNDED);

    public boolean isRefundable() {
        return REFUNDABLE_STATES.contains(this);
    }
}
