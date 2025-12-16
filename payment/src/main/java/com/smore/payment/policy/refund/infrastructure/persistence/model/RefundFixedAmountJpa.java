package com.smore.payment.policy.refund.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundFixedAmountJpa {

    @Column(name = "fixed_amount")
    private BigDecimal fixedAmount;

    public RefundFixedAmountJpa(BigDecimal fixedAmount) {
        this.fixedAmount = fixedAmount;
    }
}
