package com.smore.payment.payment.infrastructure.persistence.repository.redis;

import com.smore.payment.payment.domain.model.TemporaryPayment;
import com.smore.payment.payment.domain.repository.RedisRepository;

import java.util.Optional;
import java.util.UUID;

public class RedisRepositoryImpl implements RedisRepository {
    @Override
    public Optional<TemporaryPayment> findByOrderId(UUID uuid) {
        return Optional.empty();
    }

    @Override
    public void deleteByOrderId(UUID orderId) {

    }
}
