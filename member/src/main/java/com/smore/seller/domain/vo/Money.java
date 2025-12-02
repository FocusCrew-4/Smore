package com.smore.seller.domain.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Money {
    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public static Money of(Integer amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    public Money add(Money money) {
        return new Money(this.amount.add(money.amount));
    }

    public Money subtract(Money money) {
        return new Money(this.amount.subtract(money.amount));
    }

    public BigDecimal amount() {
        return amount;
    }
}
