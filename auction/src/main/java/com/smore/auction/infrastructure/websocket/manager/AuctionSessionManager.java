package com.smore.auction.infrastructure.websocket.manager;

public interface AuctionSessionManager {
    boolean handleSubscribe(String sessionId, Long userId, String auctionId);
    void handleDisconnect(String sessionId);
}