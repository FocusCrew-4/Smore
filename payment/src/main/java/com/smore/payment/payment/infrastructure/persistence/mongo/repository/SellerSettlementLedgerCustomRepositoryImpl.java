package com.smore.payment.payment.infrastructure.persistence.mongo.repository;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.bson.Document;

import java.math.BigDecimal;
import java.util.Arrays;

@Repository
@RequiredArgsConstructor
public class SellerSettlementLedgerCustomRepositoryImpl implements SellerSettlementLedgerCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public BigDecimal calculateBalance(Long sellerId) {

        MongoCollection<Document> collection =
                mongoTemplate.getCollection("seller_settlement_ledger");

        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(
                new Document("$match", new Document("sellerId", sellerId)),
                new Document("$group",
                        new Document("_id", null)
                                .append("balance", new Document("$sum", "$amount"))
                )
        ));

        Document balanceDoc = result.first();

        if (balanceDoc == null) {
            return BigDecimal.ZERO;
        }

        // MongoDB는 numeric 타입을 Double 로 반환하는 경우가 많다
        Object value = balanceDoc.get("balance");

        if (value instanceof Double d) {
            return BigDecimal.valueOf(d);
        }
        if (value instanceof Integer i) {
            return BigDecimal.valueOf(i);
        }
        if (value instanceof Long l) {
            return BigDecimal.valueOf(l);
        }
        if (value instanceof BigDecimal bd) {
            return bd;
        }

        throw new IllegalStateException("balance 필드 타입 예기치 않음: " + value.getClass());
    }
}
