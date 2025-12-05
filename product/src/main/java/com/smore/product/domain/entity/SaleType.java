package com.smore.product.domain.entity;

public enum SaleType {

    NORMAL("한정 판매"),
    AUCTION("경매"),
    LIMITED_TO_AUCTION("한정 판매 후 경매 전환");

    private final String desc;

    SaleType(String desc) {
        this.desc = desc;
    }

    public String desc() {
        return desc;
    }
}
