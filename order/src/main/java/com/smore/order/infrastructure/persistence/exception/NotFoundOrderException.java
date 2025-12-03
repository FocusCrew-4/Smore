package com.smore.order.infrastructure.persistence.exception;

public class NotFoundOrderException extends RuntimeException {

    public NotFoundOrderException(String message) {
        super(message);
    }
}
