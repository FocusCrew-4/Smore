package com.smore.payment.payment.domain.repository;

import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PaymentRefund;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository {
    void save(Payment payment);

    Optional<Payment> findByIdempotencyKey(UUID idempotencyKey);

    Optional<Payment> findById(UUID uuid);

    void updateRefund(UUID paymentId, PaymentRefund refund);
}
