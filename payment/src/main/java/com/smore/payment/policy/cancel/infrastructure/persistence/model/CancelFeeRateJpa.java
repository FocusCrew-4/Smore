package com.smore.payment.policy.cancel.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancelFeeRateJpa {

    @Column(name = "rate")
    private BigDecimal rate;

    public CancelFeeRateJpa(BigDecimal rate) {
        this.rate = rate;
    }
}
