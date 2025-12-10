package com.smore.order.domain.status;

import com.smore.order.presentation.auth.OrderRole;

public enum SaleType {
    BID("판매 경쟁"),
    AUCTION("경매")
    ;

    private final String description;

    SaleType(String description) {
        this.description = description;
    }

    public static SaleType from(String value) {
        return SaleType.valueOf(value);
    }
}
