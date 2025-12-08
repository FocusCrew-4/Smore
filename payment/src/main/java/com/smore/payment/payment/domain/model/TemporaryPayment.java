package com.smore.payment.payment.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class TemporaryPayment {

    private UUID idempotencyKey;
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String pgOrderId;

    public void validateApproval(BigDecimal requestAmount) {
        if (!this.amount.equals(requestAmount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    }

}