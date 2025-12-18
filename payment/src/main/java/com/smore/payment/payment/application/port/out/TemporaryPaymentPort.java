package com.smore.payment.payment.application.port.out;

import com.smore.payment.payment.domain.model.TemporaryPayment;
import java.util.Optional;
import java.util.UUID;

public interface TemporaryPaymentPort {
    Optional<TemporaryPayment> findByOrderId(UUID uuid);

    void deleteByOrderId(UUID orderId);

    void save(TemporaryPayment temp);

    void update(TemporaryPayment temp);

    boolean existsByOrderId(UUID orderId);
}
