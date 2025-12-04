package com.smore.payment.payment.domain.model;

public enum PaymentMethod {
    CARD("카드결제"),
    VIRTUAL_ACCOUNT("가상계좌"),
    TRANSFER("계좌이체"),
    MOBILE_PHONE("휴대폰결제");

    private final String desc;

    PaymentMethod(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static PaymentMethod of(String name) {
        return PaymentMethod.valueOf(name.toUpperCase());
    }
}