package com.smore.auction.infrastructure.websocket.handler.impl;

import com.smore.auction.infrastructure.websocket.handler.AuctionSubHandler;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionSubHandlerImpl implements AuctionSubHandler {

    /*
     ConcurrentMap 은 Java 에서 동시성에 안전한 Map 인터페이스
     여러 스레드가 동시에 put/remove/get 해도 안전하게 동작하도록 설계된 Map
     */
    // auctionId → Set<sessionId>
    private final ConcurrentMap<String, Set<String>> auctionRoomSessions =
        new ConcurrentHashMap<>();

    // sessionId → userId
    private final ConcurrentMap<String, Long> sessionToUser =
        new ConcurrentHashMap<>();

    // sessionId → auctionId
    private final ConcurrentMap<String, String> sessionToAuction =
        new ConcurrentHashMap<>();

    @Override
    public void handleSubscribe(String sessionId, Long userId, String auctionId) {
        // auctionId → Set<sessionId> 등록
        auctionRoomSessions
            .computeIfAbsent(auctionId, k -> ConcurrentHashMap.newKeySet())
            .add(sessionId);

        // sessionId → userId
        sessionToUser.put(sessionId, userId);

        // sessionId → auctionId
        sessionToAuction.put(sessionId, auctionId);
    }

    @Override
    public void handleDisconnect(String sessionId) {

        // sessionId → auctionId 조회 후 제거
        String auctionId = sessionToAuction.remove(sessionId);

        // sessionId → userId 제거
        sessionToUser.remove(sessionId);

        if (auctionId != null) {
            Set<String> sessions = auctionRoomSessions.get(auctionId);
            if (sessions != null) {
                sessions.remove(sessionId);

                // 방이 완전히 비었으면 제거해도 됨 (선택 사항)
                if (sessions.isEmpty()) {
                    auctionRoomSessions.remove(auctionId);
                }
            }
        }
    }
}
