package com.smore.payment.payment.infrastructure.redis;

import com.smore.payment.payment.application.port.out.TemporaryPaymentPort;
import com.smore.payment.payment.domain.model.TemporaryPayment;
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
public class TemporaryPaymentAdapter implements TemporaryPaymentPort {

    private final RedisTemplate<String, TemporaryPayment> temporaryPaymentRedisTemplate;

    @Override
    public Optional<TemporaryPayment> findByOrderId(UUID orderId) {
        log.info("레디스 찾기 시작: {}", orderId);

        String tempKey = tempKey(orderId);
        String approvedKey = approvedKey(orderId);

        TemporaryPayment temp = temporaryPaymentRedisTemplate.opsForValue().get(tempKey);

        if (temp == null) {
            temp = temporaryPaymentRedisTemplate.opsForValue().get(approvedKey);
        }

        log.info("레디스 결과: {}", temp);

        return Optional.ofNullable(temp);
    }

    @Override
    public void deleteByOrderId(UUID orderId) {
        temporaryPaymentRedisTemplate.delete(tempKey(orderId));
        temporaryPaymentRedisTemplate.delete(approvedKey(orderId));
    }

    @Override
    public void save(TemporaryPayment temp) {
        Duration ttl = Duration.between(LocalDateTime.now(), temp.getExpiredAt());

        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("만료 시간이 현재 시간보다 과거입니다.");
        }
        temporaryPaymentRedisTemplate.opsForValue()
                .set(tempKey(temp.getOrderId()), temp, ttl);
    }

    @Override
    public void update(TemporaryPayment temp) {

        String oldKey = tempKey(temp.getOrderId());
        String newKey = approvedKey(temp.getOrderId());

        Boolean exists = temporaryPaymentRedisTemplate.hasKey(oldKey);
        if (!exists) {
            throw new IllegalStateException(
                    "TemporaryPayment가 Redis에 존재하지 않습니다. orderId=" + temp.getOrderId()
            );
        }

        temporaryPaymentRedisTemplate.rename(oldKey, newKey);

        temporaryPaymentRedisTemplate.opsForValue().set(newKey, temp);

        log.info(
                "TemporaryPayment 승인 상태로 승격. key={}, pgApprovedAt={}",
                newKey,
                temp.getPgResponseResult() != null ? temp.getPgResponseResult().approvedAt() : null
        );
    }

    @Override
    public boolean existsByOrderId(UUID orderId) {
        return temporaryPaymentRedisTemplate.hasKey(tempKey(orderId)) ||
                temporaryPaymentRedisTemplate.hasKey(approvedKey(orderId));
    }

    private String tempKey(UUID orderId) {
        return "payment:temp:" + orderId;
    }

    private String approvedKey(UUID orderId) {
        return "payment:pg:approved:" + orderId;
    }
}
