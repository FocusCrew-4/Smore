package com.smore.auction.infrastructure.adapter.out;

import com.smore.auction.application.port.out.AuctionRoomRegistry;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.infrastructure.redis.RedisKeyFactory;
import java.time.Duration;
import java.util.Map;
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
    public void register(Auction auction, Duration ttl) {
        Map<String, String> meta = Map.of(
            "stock", auction.getStock().toString(),
            "minPrice", auction.getProduct().price().toString()
        );

        redis.opsForHash()
            .putAll(key.auctionOpen(auction.getId().toString()), meta);
        redis.expire(key.auctionOpen(auction.getId().toString()), ttl);
    }
}
