package com.smore.payment.payment.infrastructure.persistence.mongo.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Document(collection = "seller_settlement_ledger")
public class SellerSettlementLedger {

    @Id
    private String id;

    private Long sellerId;              // 판매자 ID

    private LedgerType type;            // EARN, REFUND, SETTLEMENT

    private BigDecimal amount;          // + 또는 -

    private UUID paymentId;             // 결제 기반 발생이면 기록 (nullable)

    private UUID idempotencyKey;        // 중복 요청 방지용 key

    private LocalDateTime timestamp;    // 기록 시간

    public enum LedgerType {
        EARN, REFUND, SETTLEMENT
    }
}
