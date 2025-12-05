package com.smore.order.domain.status;

public enum EventType {
    ORDER_CREATED("주문 생성 완료"),
    ORDER_COMPLETED("주문 완료"),
    ORDER_FAILED("주문 실패"),
    REFUND_REQUEST("환불 요청"),
    PAYMENT_CANCEL("결제 취소 요청"),
    ORDER_CANCELLED("주문 취소 완료")
    ;

    private final String description;

    EventType(String description) {
        this.description = description;
    }

}
