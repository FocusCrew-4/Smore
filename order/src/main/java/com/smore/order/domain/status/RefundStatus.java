package com.smore.order.domain.status;

public enum RefundStatus {
    REQUESTED("환불 접수"),
    PROCESSING("처리중"),
    COMPLETED("환불 완료"),
    FAILED("환불 실패")
    ;

    private String description;

    RefundStatus(String description) {
        this.description = description;
    }
}
