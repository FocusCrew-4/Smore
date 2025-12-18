package com.smore.payment.payment.infrastructure.api_inbox;

import com.smore.payment.payment.application.port.in.ApprovePaymentResult;
import com.smore.payment.payment.application.port.out.ApiInboxPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ApiInboxRepository implements ApiInboxPort {

    private final RedisTemplate<String, ApprovePaymentResult> redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(30);

    public Optional<ApprovePaymentResult> find(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(keyOf(key)));
    }

    public void save(String key, ApprovePaymentResult result) {
        redisTemplate.opsForValue().set(keyOf(key), result, TTL);
    }

    private String keyOf(String idempotencyKey) {
        return "api-inbox:payment:approve:" + idempotencyKey;
    }
}
