package com.smore.payment.payment.application.port.out;

import com.smore.payment.payment.domain.model.PgApproveResult;

import java.math.BigDecimal;

public interface PgClient {

    PgApproveResult approve(String paymentKey, String pgOrderId, BigDecimal amount);

//    PgCancelResult cancel(String transactionKey, BigDecimal amount, String reason);

//    PgRefundResult refund(String transactionKey, BigDecimal amount, String reason);
}
