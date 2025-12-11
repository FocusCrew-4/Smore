package com.smore.bidcompetition.domain.status;

public enum AggregateType {
    BID("판매 경쟁 이벤트"),
    AUCTION("경매 이벤트"),
    ORDER("주문 이벤트"),
    PAYMENT("결제 이벤트")
    ;

    private final String description;

    AggregateType(String description) {
        this.description = description;
    }
}
