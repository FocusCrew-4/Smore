package com.smore.payment.payment.infrastructure.persistence.redis.repository;

import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Optional<TemporaryPayment> findByOrderId(UUID orderId) {
        Object value = redisTemplate.opsForValue().get(orderId.toString());
        if (value instanceof TemporaryPayment temp) {
            return Optional.of(temp);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByOrderId(UUID orderId) {
        redisTemplate.delete(orderId.toString());
    }

    @Override
    public void save(TemporaryPayment temp) {
        redisTemplate.opsForValue().set(temp.getOrderId().toString(), temp, Duration.between(LocalDateTime.now(), temp.getExpiredAt()));
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return redisTemplate.hasKey(orderId.toString());
    }
}
