package com.smore.order.infrastructure.persistence.exception;

public class CreateOutboxFailException extends RuntimeException {

    public CreateOutboxFailException(String message) {
        super(message);
    }
}
