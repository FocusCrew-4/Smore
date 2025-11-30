package com.smore.order.infrastructure.persistence.exception;

public class CreateOrderFailException extends RuntimeException {

    public CreateOrderFailException(String message) {
        super(message);
    }
}
