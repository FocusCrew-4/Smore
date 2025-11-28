package com.smore.auction.domain.enums;

public enum AuctionStatus {
    READY("준비 상태"),
    OPEN("경매 진행중"),
    CLOSED("경매 종료"),
    CANCELED("경매 취소");

    private final String description;

    AuctionStatus(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
