package com.smore.auction.infrastructure.websocket.manager;


// TODO: 예외타입 수정 필요
public interface AuctionPubManager {
    void validateSend(String sessionId, String auctionId) throws IllegalAccessException;
    void registerAndGetEntryOrder(String sessionId, String auctionId);
}
