package com.smore.payment.payment.infrastructure.persistence.mongo.repository;

import com.smore.payment.payment.application.port.out.SellerSettlementLedgerRepository;
import com.smore.payment.payment.infrastructure.persistence.mongo.model.SellerSettlementLedger;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class SellerSettlementLedgerRepositoryImpl implements SellerSettlementLedgerRepository {

    private final SellerSettlementLedgerMongoRepository sellerSettlementLedgerMongoRepository;


    @Override
    public boolean existsByIdempotencyKey(UUID idempotencyKey) {
        return sellerSettlementLedgerMongoRepository.existsByIdempotencyKey(idempotencyKey);
    }

    @Override
    public BigDecimal calculateBalance(Long sellerId) {
        return sellerSettlementLedgerMongoRepository.calculateBalance(sellerId);
    }

    @Override
    public void saveLedger(
            Long sellerId,
            String type,
            BigDecimal amount,
            UUID paymentId,
            UUID idempotencyKey
    ) {
        SellerSettlementLedger ledger = SellerSettlementLedger.builder()
                .sellerId(sellerId)
                .type(SellerSettlementLedger.LedgerType.valueOf(type))
                .amount(amount)
                .paymentId(paymentId)
                .idempotencyKey(idempotencyKey)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            sellerSettlementLedgerMongoRepository.save(ledger);
        } catch (DuplicateKeyException e) {
            // 동일한 idempotencyKey로 이미 기록된 경우 - 멱등성을 보장하기 위해 무시
        }
    }
}
