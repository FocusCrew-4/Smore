package com.smore.order.infrastructure.persistence.exception;

public class NotFoundRefundException extends RuntimeException {

    public NotFoundRefundException(String message) {
        super(message);
    }
}
