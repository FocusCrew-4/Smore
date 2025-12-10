package com.smore.payment.payment.domain.repository;

import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PaymentStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository {
    void save(Payment payment);

    Optional<Payment> findByIdempotencyKey(UUID idempotencyKey);
}
