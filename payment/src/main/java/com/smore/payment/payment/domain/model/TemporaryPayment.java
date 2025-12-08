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

    public void validateApproval(BigDecimal requestAmount) {
        if (!this.amount.equals(requestAmount)) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }
    }

    protected  TemporaryPayment(
            UUID idempotencyKey,
            UUID orderId,
            Long userId,
            BigDecimal amount
    ) {
        this.idempotencyKey = idempotencyKey;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
    }

    public static TemporaryPayment create(
                    UUID idempotencyKey,
                    UUID orderId,
                    Long userId,
                    BigDecimal amount
    ){
        return new TemporaryPayment(
                idempotencyKey,
                orderId,
                userId,
                amount
        );
    }

}