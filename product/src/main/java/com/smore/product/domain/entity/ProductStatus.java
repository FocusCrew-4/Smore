package com.smore.product.domain.entity;

public enum ProductStatus {
    ON_SALE("판매중"),
    SOLD_OUT("품절"),
    INACTIVE("비활성화");

    private final String desc;

    ProductStatus(String desc) {
        this.desc = desc;
    }

    public String desc() {
        return desc;
    }
}
