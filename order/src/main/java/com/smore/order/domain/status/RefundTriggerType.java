package com.smore.order.domain.status;

public enum RefundTriggerType {

    USER_REQUEST("유저 요청"),
    INVENTORY_CONFIRM_TIMEOUT("재고 확정 타임아웃")
    ;

    private final String description;

    RefundTriggerType(String description) {
        this.description = description;
    }

}
