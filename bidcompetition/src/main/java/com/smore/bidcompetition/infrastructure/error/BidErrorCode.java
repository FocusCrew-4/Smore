package com.smore.bidcompetition.infrastructure.error;

import com.smore.common.error.ErrorCode;

public enum BidErrorCode implements ErrorCode {
    NOT_FOUND_BID("70404", "Bid를 찾을 수 없습니다."),
    NOT_FOUND_OUTBOX("71404", "Outbox를 찾을 수 없습니다."),



    CREATE_OUTBOX_CONFLICT("71409", "Outbox를 생성하던 도중 예외가 발생했습니다."),
    ;

    private final String code;
    private final String message;
    private final String triggeredBy;

    BidErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
        this.triggeredBy = ErrorCode.super.triggeredBy();
    }

    @Override
    public String code() { return code; }

    @Override
    public String message() { return message; }

    @Override
    public String triggeredBy() { return triggeredBy; }

}
