package com.smore.order.infrastructure.error;

import com.smore.common.error.ErrorCode;

public enum OrderErrorCode implements ErrorCode {
    NOT_FOUND_ORDER("50404", "Order를 찾을 수 없습니다."),
    NOT_FOUND_OUTBOX("51404", "Outbox를 찾을 수 없습니다."),
    NOT_FOUND_REFUND("52404", "Refund를 찾을 수 없습니다."),

    CREATE_ORDER_CONFLICT("50409", "Order를 생성하던 도중 예외가 발생했습니다."),
    CREATE_OUTBOX_CONFLICT("51409", "Outbox를 생성하던 도중 예외가 발생했습니다."),
    COMPLETE_ORDER_CONFLICT("52409", "주문 완료 도중 예외가 발생했습니다ㅣ."),
    UPDATE_ORDER_FAIL_CONFLICT("53409", "주문 정보를 업데이트 하던 도중 예외가 발생했습니다."),
    ORDER_ID_MISMATCH("54409", "주문 ID가 일치하지 않습니다."),
    REFUND_RESERVATION_CONFLICT("55409", "환불 예약 도중 예외가 발생했습니다.")
    ;

    private final String code;
    private final String message;
    private final String triggeredBy;

    OrderErrorCode(String code, String message) {
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
