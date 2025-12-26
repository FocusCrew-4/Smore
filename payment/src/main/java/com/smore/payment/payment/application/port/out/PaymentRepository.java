package com.smore.payment.payment.application.port.out;

import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PaymentRefund;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    void save(Payment payment);

    Optional<Payment> findByIdempotencyKey(UUID idempotencyKey);

    Optional<Payment> findById(UUID uuid);

    void updateRefund(UUID paymentId, PaymentRefund refund);
}
