package com.smore.bidcompetition.infrastructure.redis;

import org.springframework.stereotype.Component;

@Component
public class StockRedisArgs {

    // Object[] 타입을 반환하는 이유는 RedisTemplate.execute()의 시그니처를 맞추기 위함
    public Object[] reserveArgs(String userId, int quantity, long winnerTtlSeconds, long idemTtlSeconds) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("userId는 필수값입니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
        if (winnerTtlSeconds <= 0) {
            throw new IllegalArgumentException("winnerTtlSeconds는 1 이상이어야 합니다.");
        }
        if (idemTtlSeconds <= 0) {
            throw new IllegalArgumentException("idemTtlSeconds는 1 이상이어야 합니다.");
        }

        return new Object[] {
            userId,
            String.valueOf(quantity),
            String.valueOf(winnerTtlSeconds),
            String.valueOf(idemTtlSeconds)
        };
    }

    public Object[] rollbackArgs(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity는 1 이상이어야 합니다.");
        }
        return new Object[] { String.valueOf(quantity) };
    }

    public Object[] confirmArgs() {
        return new Object[0];
    }

}
