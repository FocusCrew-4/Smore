package com.smore.auction.infrastructure.websocket.handler;

public interface AuctionSubHandler {
    void handleSubscribe(String sessionId, Long userId, String auctionId);
    void handleDisconnect(String sessionId);
}