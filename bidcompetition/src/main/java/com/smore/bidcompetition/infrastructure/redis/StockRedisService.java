package com.smore.bidcompetition.infrastructure.redis;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockRedisService {

    private final StringRedisTemplate redis;
    private final DefaultRedisScript<Long> reserveStockScript;
    private final DefaultRedisScript<Long> rollbackRestoreScript;
    private final DefaultRedisScript<Long> refundRestoreScript;
    private final DefaultRedisScript<Long> confirmCleanupScript;
    private final DefaultRedisScript<Long> setStockScript;
    private final StockRedisKeys keys;
    private final StockRedisArgs args;

    public long reserve(UUID bidId, String allocationKey, String idemKey, String userId,
        int quantity, long ttl) {

        return redis.execute(
            reserveStockScript,
            List.of(
                keys.stockKey(bidId),
                keys.winnerKey(bidId, allocationKey),
                keys.idemKey(bidId, idemKey)
            ),
            args.reserveArgs(userId, quantity, ttl, ttl)
        );
    }

    public long rollback(UUID bidId, String allocationKey, String idemKey, int quantity) {
        return redis.execute(
            rollbackRestoreScript,
            List.of(
                keys.stockKey(bidId),
                keys.winnerKey(bidId, allocationKey),
                keys.idemKey(bidId, idemKey)
            ),
            args.rollbackArgs(quantity)
        );
    }

    public long refundRestore(UUID bidId, UUID refundId, int quantity) {
        return redis.execute(
            refundRestoreScript,
            List.of(
                keys.stockKey(bidId),
                keys.refundKey(bidId, refundId)
            ),
            args.rollbackArgs(quantity)
        );
    }

    public void confirmCleanup(UUID bidId, String allocationKey, String idemKey) {
        redis.execute(
            confirmCleanupScript,
            List.of(
                keys.winnerKey(bidId, allocationKey),
                keys.idemKey(bidId, idemKey)
            )
        );
    }

    public long setStock(UUID bidId, int stockQuantity) {
        return redis.execute(
            setStockScript,
            List.of(keys.stockKey(bidId)),
            String.valueOf(stockQuantity)
        );
    }

    public boolean deleteStock(UUID bidId) {
        Boolean deleted = redis.delete(keys.stockKey(bidId));
        return Boolean.TRUE.equals(deleted);
    }
}
