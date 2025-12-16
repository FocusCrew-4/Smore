package com.smore.payment.payment.domain.repository;

import com.smore.payment.payment.infrastructure.persistence.redis.model.TemporaryPayment;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RedisRepository {
    Optional<TemporaryPayment> findByOrderId(UUID uuid);

    void deleteByOrderId(UUID orderId);

    void save(TemporaryPayment temp);

    boolean existsByOrderId(UUID orderId);
}
