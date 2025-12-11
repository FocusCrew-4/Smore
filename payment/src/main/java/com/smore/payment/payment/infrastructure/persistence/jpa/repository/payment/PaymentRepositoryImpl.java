package com.smore.payment.payment.infrastructure.persistence.jpa.repository.payment;

import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.repository.PaymentRepository;
import com.smore.payment.payment.infrastructure.persistence.jpa.mapper.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public void save(Payment payment) {
        paymentJpaRepository.save(paymentMapper.toEntity(payment));
    }

    @Override
    public Optional<Payment> findByIdempotencyKey(UUID idempotencyKey) {
        return paymentJpaRepository.findByIdempotencyKey(idempotencyKey)
                .map(paymentMapper::toDomainEntity);
    }
}
