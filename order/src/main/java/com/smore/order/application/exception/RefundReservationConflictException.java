package com.smore.order.application.exception;

public class RefundReservationConflictException extends RuntimeException {

    public RefundReservationConflictException(String message) {
        super(message);
    }
}
