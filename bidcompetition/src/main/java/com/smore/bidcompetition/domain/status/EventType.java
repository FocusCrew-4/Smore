package com.smore.bidcompetition.domain.status;

public enum EventType {
    BID_WINNER_SELECTED("경쟁 승리자 선정")
    ;

    private final String description;

    EventType(String description) {
        this.description = description;
    }

}
