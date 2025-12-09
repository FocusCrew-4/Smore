package com.smore.auction.infrastructure.websocket.manager.impl;

import com.smore.auction.infrastructure.websocket.manager.AuctionPubManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionPubManagerImpl implements AuctionPubManager {

    private final StringRedisTemplate redis;

    // ws:auction:501 = {sessionA, sessionB, sessionC}
    private String keyAuction_sessions(String auctionId) {
        return "ws:auction:" + auctionId;
    }

    // ws:session:abc123:user = 10
    private String keySession_user(String sessionId) {
        return "ws:session:" + sessionId + ":user";
    }

    // ws:session:abc123:auction = 501, 202, 333
    private String keySession_auctions(String sessionId) {
        return "ws:session:" + sessionId + ":auction";
    }


    @Override
    public void registerAndGetEntryOrder(String sessionId, String auctionId) {

    }

    @Override
    public void validateSend(String sessionId, String auctionId) throws IllegalAccessException {
        Boolean exists = redis.opsForSet()
            .isMember(keyAuction_sessions(auctionId), sessionId);

        if (exists == null || !exists) {
            throw new IllegalAccessException("세션이 해당 경매방에 참여 중이 아닙니다");
        }
    }
}
