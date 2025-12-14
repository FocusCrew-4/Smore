package com.smore.payment.payment.infrastructure.persistence.mongo.repository;

import java.math.BigDecimal;

public interface SellerSettlementLedgerCustomRepository {
    BigDecimal calculateBalance(Long sellerId);
}
