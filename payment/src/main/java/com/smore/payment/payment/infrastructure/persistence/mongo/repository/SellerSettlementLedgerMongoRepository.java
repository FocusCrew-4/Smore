package com.smore.payment.payment.infrastructure.persistence.mongo.repository;

import com.smore.payment.payment.infrastructure.persistence.mongo.model.SellerSettlementLedger;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.math.BigDecimal;
import java.util.UUID;

public interface SellerSettlementLedgerMongoRepository extends MongoRepository<SellerSettlementLedger, String>, SellerSettlementLedgerCustomRepository{
    boolean existsByIdempotencyKey(UUID idempotencyKey);
}
