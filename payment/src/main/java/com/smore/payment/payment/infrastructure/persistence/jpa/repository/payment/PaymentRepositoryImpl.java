package com.smore.payment.payment.infrastructure.persistence.jpa.repository.payment;

import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PaymentRefund;
import com.smore.payment.payment.application.port.out.PaymentRepository;
import com.smore.payment.payment.infrastructure.persistence.jpa.mapper.PaymentMapper;
import com.smore.payment.payment.infrastructure.persistence.jpa.model.payment.PaymentEntity;
import com.smore.payment.payment.infrastructure.persistence.jpa.model.payment.PaymentRefundJpa;
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

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return paymentJpaRepository.findById(paymentId).map(paymentMapper::toDomainEntity);
    }

    @Override
    public void updateRefund(UUID paymentId, PaymentRefund refund) {
        PaymentEntity entity = paymentJpaRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        entity.setRefund(new PaymentRefundJpa(
                refund.reason(),
                refund.cancelAmount(),
                refund.refundedAt(),
                refund.pgCancelTransactionKey(),
                refund.refundableAmount()
        ));

        paymentJpaRepository.save(entity);
    }


}

