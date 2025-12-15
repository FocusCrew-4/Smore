package com.smore.auction.domain.enums;

public enum BidderStatus {
    WINNER("현재 낙찰권 보유"),
    STANDBY("차순위 대기자"),
    CONFIRMED("주문 생성 완료"),
    CANCELLED("권리 보유했지만 거절/환불/기한초과");

    private final String description;

    BidderStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
