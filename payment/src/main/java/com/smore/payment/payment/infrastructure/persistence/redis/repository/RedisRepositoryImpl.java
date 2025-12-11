package com.smore.payment.payment.infrastructure.persistence.redis.repository;

import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, TemporaryPayment> temporaryPaymentRedisTemplate;

    @Override
    public Optional<TemporaryPayment> findByOrderId(UUID orderId) {
        log.info("레디스 찾기 시작: {}", orderId);

        TemporaryPayment temp = temporaryPaymentRedisTemplate.opsForValue()
                .get(orderId.toString());

        log.info("레디스 결과: {}", temp);

        return Optional.ofNullable(temp);
    }

    @Override
    public void deleteByOrderId(UUID orderId) {
        temporaryPaymentRedisTemplate.delete(orderId.toString());
    }

    @Override
    public void save(TemporaryPayment temp) {
        Duration ttl = Duration.between(LocalDateTime.now(), temp.getExpiredAt());

        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("만료 시간이 현재 시간보다 과거입니다.");
        }

        temporaryPaymentRedisTemplate.opsForValue().set(temp.getOrderId().toString(), temp, ttl);
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return temporaryPaymentRedisTemplate.hasKey(orderId.toString());
    }
}
