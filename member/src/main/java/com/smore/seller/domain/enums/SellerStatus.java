package com.smore.seller.domain.enums;

public enum SellerStatus {
    PENDING("승인 대기"),
    REJECTED("승인 거절"),
    ACTIVE("활성 판매자"),
    INACTIVE("비활성 판매자"),
    DELETED("삭제된 판매자"),
    BANNED("정지된 판매자");

    private final String desc;

    SellerStatus(String desc) {
        this.desc = desc;
    }

    public String desc() {
        return desc;
    }
}
