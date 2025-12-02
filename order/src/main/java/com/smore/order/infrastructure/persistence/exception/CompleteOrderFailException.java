package com.smore.order.infrastructure.persistence.exception;

public class CompleteOrderFailException extends RuntimeException {

    public CompleteOrderFailException(String message) {
        super(message);
    }
}
