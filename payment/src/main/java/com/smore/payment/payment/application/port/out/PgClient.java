package com.smore.payment.payment.application.port.out;

import com.smore.payment.payment.domain.model.PgResponseResult;

import java.math.BigDecimal;

public interface PgClient {

    PgResponseResult approve(String paymentKey, String pgOrderId, BigDecimal amount);

    PgResponseResult refund(String paymentKey, BigDecimal refundAmount, String refundReason);

}
