package com.smore.bidcompetition.infrastructure.redis;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class StockRedisKeys {

    private static final String STOCK_PREFIX = "stock";
    private static final String WINNER_PREFIX = "winner";
    private static final String IDEM_PREFIX = "idem";


    public String stockKey(UUID bidId) {
        if (bidId == null) throw new IllegalArgumentException("bidId는 필수값입니다.");
        return STOCK_PREFIX + ":{" + bidId + "}";
    }

    public String winnerKey(UUID bidId, String allocationKey) {
        if (bidId == null) throw new IllegalArgumentException("bidId는 필수값입니다.");
        if (allocationKey == null) throw new IllegalArgumentException("allocationKey는 필수값입니다.");
        return WINNER_PREFIX + ":{" + bidId + "}:" + allocationKey;
    }

    public String idemKey(UUID bidId, String idempotencyKey) {
        if (bidId == null) throw new IllegalArgumentException("bidId는 필수값입니다.");
        if (idempotencyKey == null) throw new IllegalArgumentException("idempotencyKey는 필수값입니다.");
        return IDEM_PREFIX + ":{" + bidId + "}:" + idempotencyKey;
    }
}
