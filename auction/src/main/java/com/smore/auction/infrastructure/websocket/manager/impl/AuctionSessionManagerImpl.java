package com.smore.auction.infrastructure.websocket.manager.impl;

import com.smore.auction.infrastructure.websocket.manager.AuctionSessionManager;
import java.time.Duration;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionSessionManagerImpl implements AuctionSessionManager {

    private final StringRedisTemplate redis;

    /*
    Redis 네이밍 룰 [namespace]:[entity]:[identifier]:[property]
     */
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

    /*
     ConcurrentMap 은 Java 에서 동시성에 안전한 Map 인터페이스
     여러 스레드가 동시에 put/remove/get 해도 안전하게 동작하도록 설계된 Map
     */
    // region 지식으로만 알고있으면 됩니다
    // auctionId → Set<sessionId>
//    private final ConcurrentMap<String, Set<String>> auctionRoomSessions =
//        new ConcurrentHashMap<>();
//
//    // sessionId → userId
//    private final ConcurrentMap<String, Long> sessionToUser =
//        new ConcurrentHashMap<>();
//
//    // sessionId → auctionId
//    private final ConcurrentMap<String, String> sessionToAuction =
//        new ConcurrentHashMap<>();
    //endregion

    @Override
    public void handleSubscribe(String sessionId, Long userId, String auctionId) {
        log.info("구독매니저 진입 레디스에 키 정보 기록");

        Boolean auctionExist = redis.hasKey(keyAuction_sessions(auctionId));

        redis.opsForSet()
            .add(keyAuction_sessions(auctionId), sessionId);
        redis.opsForValue()
            .set(keySession_user(sessionId), userId.toString());
        redis.opsForSet()
            .add(keySession_auctions(sessionId), auctionId);

        if (!auctionExist) {
            redis.expire(keyAuction_sessions(auctionId), Duration.ofMinutes(10));
        }
    }

    @Override
    public void handleDisconnect(String sessionId) {

        Set<String> auctionIds = redis.opsForSet()
            .members(keySession_auctions(sessionId));

        // sessionId 키를 제거함 (매핑된 auctionId 도 지워짐)
        redis.delete(keySession_auctions(sessionId));

        // sessionId 키를 제거함 (매핑된 userId 도 지워짐)
        redis.delete(keySession_user(sessionId));

        // AuctionId 키 값들을 불러와서 내부의 sessionId 를 지움
        // 키를 지우면 내부 데이터가 모두 날아가서 하면 안 됨
        if (auctionIds != null) {
            auctionIds.forEach(auctionId -> redis.opsForSet()
                .remove(keyAuction_sessions(auctionId), sessionId));
        }
    }
}
