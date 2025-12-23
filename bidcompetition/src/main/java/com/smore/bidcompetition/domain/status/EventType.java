package com.smore.bidcompetition.domain.status;

public enum EventType {
    BID_WINNER_SELECTED("경쟁 승리자 선정"),
    PRODUCT_INVENTORY_ADJUSTED("환불"),
    BID_RESULT_FINALIZED("경쟁 결과 최종 확정"),
    BID_INVENTORY_CONFIRM_TIMEOUT("재고 확보 실패")
    ;

    private final String description;

    EventType(String description) {
        this.description = description;
    }

}
