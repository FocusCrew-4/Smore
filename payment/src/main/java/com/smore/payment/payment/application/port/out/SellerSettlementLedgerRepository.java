package com.smore.payment.payment.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

public interface SellerSettlementLedgerRepository {
    boolean existsByIdempotencyKey(UUID idempotencyKey);

    BigDecimal calculateBalance(Long sellerId);

    void saveLedger(
            Long sellerId,
            String type,
            BigDecimal amount,
            UUID paymentId,
            UUID idempotencyKey
    );
}
