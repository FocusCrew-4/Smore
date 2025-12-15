package com.smore.bidcompetition.domain.status;

public enum EventType {
    BID_WINNER_SELECTED("경쟁 승리자 선정"),
    PRODUCT_INVENTORY_ADJUSTED("환불")
    ;

    private final String description;

    EventType(String description) {
        this.description = description;
    }

}
