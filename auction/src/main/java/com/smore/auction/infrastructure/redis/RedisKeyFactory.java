package com.smore.auction.infrastructure.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyFactory {

    private static final String PREFIX_WS_AUCTION = "ws:auction:";
    private static final String PREFIX_WS_SESSION = "ws:session:";

    /*
    Redis 네이밍 룰 [namespace]:[entity]:[identifier]:[property]
     */

    /**
     * ws:auction:501 = {sessionA, sessionB, sessionC}
     * @param auctionId String
     * @return ws:auction:{auctionId}
     */
    public String auctionSessions(String auctionId) {
        return PREFIX_WS_AUCTION + auctionId;
    }

    /**
     * ws:session:abc123:user = 10
     * @param sessionId String
     * @return ws:session:{sessionId}:user
     */
    public String sessionUser(String sessionId) {
        return PREFIX_WS_SESSION + sessionId + ":user";
    }

    /**
     * ws:session:abc123:auction = 501, 202, 333
     * @param sessionId String
     * @return ws:session:{sessionId}:auction
     */
    public String sessionAuctions(String sessionId) {
        return PREFIX_WS_SESSION + sessionId + ":auction";
    }

    /**
     *
     * @param auctionId String
     * @return auction:{auctionId}:bids
     */
    public String auctionBids(String auctionId) {
        return "auction:" + auctionId + ":bids";
    }

    /**
     *
     * @param auctionId String
     * @return auction:{auctionId}:open
     */
    public String auctionOpen(String auctionId) {
        return "auction:" + auctionId + ":open";
    }
}
