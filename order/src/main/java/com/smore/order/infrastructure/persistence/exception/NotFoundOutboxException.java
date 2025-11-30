package com.smore.order.infrastructure.persistence.exception;

public class NotFoundOutboxException extends RuntimeException {

    public NotFoundOutboxException(String message) {
        super(message);
    }
}
