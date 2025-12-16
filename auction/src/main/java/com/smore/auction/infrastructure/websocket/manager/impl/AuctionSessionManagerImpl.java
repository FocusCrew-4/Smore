package com.smore.auction.infrastructure.websocket.manager.impl;

import com.smore.auction.infrastructure.redis.RedisKeyFactory;
import com.smore.auction.infrastructure.websocket.manager.AuctionSessionManager;
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
    private final RedisKeyFactory key;

    @Override
    public void handleSubscribe(String sessionId, Long userId, String auctionId) {
        log.info("구독매니저 진입 경매진행 중인지 확인 후 sub");

        Boolean auctionExist = redis.hasKey(key.auctionOpenStock(auctionId));
        log.info("경매방 검증: {}", redis.hasKey(key.auctionOpenStock(auctionId)));
        if (!auctionExist) {
            return;
        }
        // 해당 경매에 참여중인 세션으로 기록 (메시지 발송용)
        redis.opsForSet()
            .add(key.auctionSessions(auctionId), sessionId);
        // 세션이 어느 옥션에 참여중인지 기록 (삭제용)
        redis.opsForSet()
            .add(key.sessionAuctions(sessionId), auctionId);
        // 세션이 어느 유저Id 로 들어왔는지 기록
        redis.opsForValue()
            .set(key.sessionUser(sessionId), userId.toString());
    }

    @Override
    public void handleDisconnect(String sessionId) {
        log.info("디스코넥트 처리중");
        Set<String> auctionIds = redis.opsForSet()
            .members(key.sessionAuctions(sessionId));

        // sessionId 키를 제거함 (매핑된 auctionId 도 지워짐)
        redis.delete(key.sessionAuctions(sessionId));

        // sessionId 키를 제거함 (매핑된 userId 도 지워짐)
        redis.delete(key.sessionUser(sessionId));

        // AuctionId 키 값들을 불러와서 내부의 sessionId 를 지움
        // 키를 지우면 내부 데이터가 모두 날아가서 하면 안 됨
        if (auctionIds != null) {
            auctionIds.forEach(auctionId -> redis.opsForSet()
                .remove(key.auctionSessions(auctionId), sessionId));
        }
    }
}
