package com.smore.payment.payment.application.port.in;

import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;

public interface RefundPaymentUseCase {
    void refund(PaymentRefundEvent event);
}
