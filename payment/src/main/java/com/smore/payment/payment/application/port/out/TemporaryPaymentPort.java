package com.smore.payment.payment.application.port.out;

import com.smore.payment.payment.infrastructure.persistence.redis.model.TemporaryPayment;

import java.util.Optional;
import java.util.UUID;

public interface TemporaryPaymentPort {
    Optional<TemporaryPayment> findByOrderId(UUID uuid);

    void deleteByOrderId(UUID orderId);

    void save(TemporaryPayment temp);

    boolean existsByOrderId(UUID orderId);
}
