package com.smore.auction.infrastructure.websocket.manager.impl;

import com.smore.auction.infrastructure.redis.RedisKeyFactory;
import com.smore.auction.infrastructure.websocket.manager.AuctionPubManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionPubManagerImpl implements AuctionPubManager {

    private final StringRedisTemplate redis;
    private final RedisKeyFactory key;

    @Override
    public void registerAndGetEntryOrder(String sessionId, String auctionId) {

    }

    @Override
    public void validateSend(String sessionId, String auctionId) throws IllegalAccessException {
        Boolean exists = redis.opsForSet()
            .isMember(key.auctionSessions(auctionId), sessionId);

        if (exists == null || !exists) {
            throw new IllegalAccessException("세션이 해당 경매방에 참여 중이 아닙니다");
        }
    }
}
