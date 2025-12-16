package com.smore.seller.application.usecase;

import java.math.BigDecimal;

public interface CreditSellerMoney {
    void credit(Long targetId, BigDecimal amount);
}
