package com.smore.auction.infrastructure.websocket.handler.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

// TODO: 공부 필요
@Testcontainers
class AuctionSubHandlerImplTest {

    @Container
    static GenericContainer<?> redisContainer =
        new GenericContainer<>(DockerImageName.parse("redis:7.2.4"))
            .withExposedPorts(6379);

    private String auctionSessionsKey(String auctionId) {
        return "ws:auction:" + auctionId;
    }

    private String sessionUserKey(String sessionId) {
        return "ws:session:" + sessionId + ":user";
    }

    private String sessionAuctionKey(String sessionId) {
        return "ws:session:" + sessionId + ":auction";
    }

    private StringRedisTemplate redis;
    private AuctionSubHandlerImpl auctionSubHandler;

    @BeforeEach
    void cleanRedis() {
        LettuceConnectionFactory cf =
            new LettuceConnectionFactory(redisContainer.getHost(), redisContainer.getMappedPort(6379));
        cf.afterPropertiesSet();
        redis = new StringRedisTemplate(cf);
        auctionSubHandler = new AuctionSubHandlerImpl(redis);

        redis.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("“경매방이 처음 생성될 때는 TTL이 걸려야 한다 + 모든 매핑이 정상 추가되는가?”")
    void handleSubscribe_addsMappingsAndSetsExpireWhenAuctionKeyIsNew() {
        String sessionId = "session-1";
        Long userId = 10L;
        String auctionId = "auction-1";

        auctionSubHandler.handleSubscribe(sessionId, userId, auctionId);

        String auctionKey = auctionSessionsKey(auctionId);
        String sessionUserKey = sessionUserKey(sessionId);
        String sessionAuctionKey = sessionAuctionKey(sessionId);

        Set<String> auctionMembers = redis.opsForSet().members(auctionKey);
        String userMapping = redis.opsForValue().get(sessionUserKey);
        Set<String> auctionMapping = redis.opsForSet().members(sessionAuctionKey);

        assertThat(auctionMembers).containsExactly(sessionId);
        assertThat(userMapping).isEqualTo(userId.toString());
        assertThat(auctionMapping).containsExactly(auctionId);
        assertThat(redis.getExpire(auctionKey, TimeUnit.MINUTES)).isGreaterThan(0);
    }

    @Test
    @DisplayName("“이미 존재하는 경매방에 새로운 세션이 들어와도 기존 TTL을 변경하면 안 된다.”")
    void handleSubscribe_keepsExistingTtlWhenAuctionKeyAlreadyExists() {
        String sessionId = "session-2";
        Long userId = 20L;
        String auctionId = "auction-2";
        String auctionKey = auctionSessionsKey(auctionId);
        redis.opsForSet().add(auctionKey, "pre-existing-session");
        redis.expire(auctionKey, Duration.ofMinutes(1));

        auctionSubHandler.handleSubscribe(sessionId, userId, auctionId);

        assertThat(redis.opsForSet().isMember(auctionKey, sessionId)).isTrue();
        assertThat(redis.opsForValue().get(sessionUserKey(sessionId)))
            .isEqualTo(userId.toString());
        assertThat(redis.opsForSet().isMember(sessionAuctionKey(sessionId), auctionId)).isTrue();
        assertThat(redis.getExpire(auctionKey, TimeUnit.MINUTES)).isLessThanOrEqualTo(1);
    }

    @Test
    @DisplayName("“세션이 끊기면 관련된 모든 키를 삭제하고, 방에서도 세션이 제거되는가?”")
    void handleDisconnect_removesSessionKeysAndAuctionMembership() {
        String sessionId = "session-3";
        String auctionId = "auction-3";
        String auctionKey = auctionSessionsKey(auctionId);
        redis.opsForSet().add(auctionKey, sessionId);
        redis.opsForValue().set(sessionUserKey(sessionId), "10");
        redis.opsForSet().add(sessionAuctionKey(sessionId), auctionId);

        auctionSubHandler.handleDisconnect(sessionId);

        assertThat(redis.opsForSet().isMember(auctionKey, sessionId)).isFalse();
        assertThat(redis.opsForValue().get(sessionUserKey(sessionId))).isNull();
        assertThat(redis.opsForSet().members(sessionAuctionKey(sessionId))).isEmpty();
    }

    @Test
    @DisplayName("“세션이 여러 방에 참여한 경우, 모든 방의 membership 이 제거되는가?”")
    void handleDisconnect_removesSessionFromMultipleAuctionsIfExists() {
        String sessionId = "session-5";
        String auctionA = "auction-A";
        String auctionB = "auction-B";

        redis.opsForSet().add(auctionSessionsKey(auctionA), sessionId);
        redis.opsForSet().add(auctionSessionsKey(auctionB), sessionId);
        redis.opsForValue().set(sessionUserKey(sessionId), "21");
        redis.opsForSet().add(sessionAuctionKey(sessionId), auctionA, auctionB);

        auctionSubHandler.handleDisconnect(sessionId);

        assertThat(redis.opsForSet().isMember(auctionSessionsKey(auctionA), sessionId)).isFalse();
        assertThat(redis.opsForSet().isMember(auctionSessionsKey(auctionB), sessionId)).isFalse();
        assertThat(redis.opsForSet().members(sessionAuctionKey(sessionId))).isEmpty();
        assertThat(redis.opsForValue().get(sessionUserKey(sessionId))).isNull();
    }

    @Test
    @DisplayName("“session → auction 매핑이 없는 경우라도 disconnect 는 문제 없이 key 정리만 수행해야 한다.”")
    void handleDisconnect_onlyCleansSessionKeysWhenAuctionIdIsMissing() {
        String sessionId = "session-4";
        redis.opsForValue().set(sessionUserKey(sessionId), "11");
        // intentionally not setting auction mapping to simulate missing auction id

        auctionSubHandler.handleDisconnect(sessionId);

        assertThat(redis.opsForValue().get(sessionUserKey(sessionId))).isNull();
        assertThat(redis.opsForSet().members(sessionAuctionKey(sessionId))).isEmpty();
    }
}
