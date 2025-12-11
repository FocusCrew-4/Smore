package com.smore.auction.infrastructure.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyFactory {

    private static final String PREFIX_WS_AUCTION = "ws:auction:";
    private static final String PREFIX_WS_SESSION = "ws:session:";

    // ws:auction:{auctionId}
    // ws:auction:501 = {sessionA, sessionB, sessionC}
    public String auctionSessions(String auctionId) {
        return PREFIX_WS_AUCTION + auctionId;
    }

    // ws:session:{sessionId}:user
    // ws:session:abc123:user = 10
    public String sessionUser(String sessionId) {
        return PREFIX_WS_SESSION + sessionId + ":user";
    }

    // ws:session:{sessionId}:auction
    // ws:session:abc123:auction = 501, 202, 333
    public String sessionAuctions(String sessionId) {
        return PREFIX_WS_SESSION + sessionId + ":auction";
    }

    // auction:{auctionId}:bids
    public String auctionBids(String auctionId) {
        return "auction:" + auctionId + ":bids";
    }
}
