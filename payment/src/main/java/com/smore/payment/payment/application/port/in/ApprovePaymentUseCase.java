package com.smore.payment.payment.application.port.in;

public interface ApprovePaymentUseCase {
    ApprovePaymentResult approve(ApprovePaymentCommand command);
}
