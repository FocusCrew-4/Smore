package com.smore.payment.feepolicy.infrastructure.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeeRateJpa {

    @Column(name = "rate")
    private BigDecimal rate;

    public FeeRateJpa(BigDecimal rate) {
        this.rate = rate;
    }
}
