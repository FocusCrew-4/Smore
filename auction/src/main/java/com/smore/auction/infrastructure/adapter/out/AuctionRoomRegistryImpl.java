package com.smore.auction.infrastructure.adapter.out;

import com.smore.auction.application.port.out.AuctionRoomRegistry;
import com.smore.auction.infrastructure.redis.RedisKeyFactory;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionRoomRegistryImpl implements AuctionRoomRegistry {

    private final RedisKeyFactory key;
    private final StringRedisTemplate redis;

    @Override
    public void register(UUID auctionId, Duration ttl) {
        redis.opsForValue()
            .set(key.auctionOpen(String.valueOf(auctionId)), "open", ttl);
    }
}
