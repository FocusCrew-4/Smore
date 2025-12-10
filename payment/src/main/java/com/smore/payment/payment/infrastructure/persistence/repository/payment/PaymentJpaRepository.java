package com.smore.payment.payment.infrastructure.persistence.repository.payment;

import com.smore.payment.payment.infrastructure.persistence.model.payment.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {
    Optional<PaymentEntity> findByIdempotencyKey(UUID idempotencyKey);
}
