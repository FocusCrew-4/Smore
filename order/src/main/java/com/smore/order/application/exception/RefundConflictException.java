package com.smore.order.application.exception;

public class RefundConflictException extends RuntimeException {

    public RefundConflictException(String message) {
        super(message);
    }
}
