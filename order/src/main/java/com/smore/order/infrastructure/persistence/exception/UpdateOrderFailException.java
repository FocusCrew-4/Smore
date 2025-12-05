package com.smore.order.infrastructure.persistence.exception;

public class UpdateOrderFailException extends RuntimeException {

    public UpdateOrderFailException(String message) {
        super(message);
    }
}
