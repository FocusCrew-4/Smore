package com.smore.payment.payment.application.port.in;

import com.smore.payment.payment.application.event.inbound.PaymentSettlementRequestEvent;

public interface SettlePaymentUseCase {
    void settle(PaymentSettlementRequestEvent event);
}
