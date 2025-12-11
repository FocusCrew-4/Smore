package com.smore.payment.payment.domain.model;

public enum PaymentStatus {
    REQUESTED("요청됨"),
    APPROVED("승인됨"),
    FAILED("실패"),
    CANCELLED("취소됨"),
    REFUNDED("환불됨");

    private final String desc;

    PaymentStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static PaymentStatus of(String name) {
        return PaymentStatus.valueOf(name.toUpperCase());
    }
}
