package com.smore.order.infrastructure.persistence.exception;

import com.smore.order.presentation.advice.UpdateConflictException;

public class CompleteOrderFailException extends UpdateConflictException {

    public CompleteOrderFailException(String message) {
        super(message);
    }
}
