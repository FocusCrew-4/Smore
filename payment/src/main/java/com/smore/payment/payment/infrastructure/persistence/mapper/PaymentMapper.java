package com.smore.payment.payment.infrastructure.persistence.mapper;

import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PaymentCancel;
import com.smore.payment.payment.domain.model.PaymentFailure;
import com.smore.payment.payment.domain.model.PaymentRefund;
import com.smore.payment.payment.infrastructure.persistence.model.PaymentCancelJpa;
import com.smore.payment.payment.infrastructure.persistence.model.PaymentEntity;
import com.smore.payment.payment.infrastructure.persistence.model.PaymentFailureJpa;
import com.smore.payment.payment.infrastructure.persistence.model.PaymentRefundJpa;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentEntity toEntity(Payment payment) {
        PaymentFailureJpa paymentFailureJpa = (payment.getFailure() == null)
                ? null
                : PaymentFailureJpa.of(
                payment.getFailure().reason(),
                payment.getFailure().failedAt()
        );
        PaymentCancelJpa paymentCancelJpa = (payment.getCancel() == null)
                ? null
                : PaymentCancelJpa.of(
                payment.getCancel().reason(),
                payment.getCancel().cancelAmount(),
                payment.getCancel().cancelledAt(),
                payment.getCancel().pgCancelTransactionId()
        );
        PaymentRefundJpa paymentRefundJpa = (payment.getRefund() == null)
                ? null
                : PaymentRefundJpa.of(
                payment.getRefund().reason(),
                payment.getRefund().refundAmount(),
                payment.getRefund().refundedAt()
        );
        return new PaymentEntity(
                payment.getId(),
                payment.getIdempotencyKey(),
                payment.getUserId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getApprovedAt(),
                payment.getCardCompany(),
                payment.getCardNumber(),
                payment.getInstallmentMonths(),
                payment.isInterestFree(),
                payment.getCardType(),
                payment.getOwnerType(),
                payment.getCardCompanyCode(),
                payment.getAcquirerCode(),
                paymentFailureJpa,
                paymentCancelJpa,
                paymentRefundJpa,
                payment.getPgProvider(),
                payment.getPgOrderId(),
                payment.getPgTransactionKey(),
                payment.getPgStatus(),
                payment.getPgMessage()
        );
    }

    public Payment toDomainEntity(PaymentEntity paymentEntity) {
        PaymentFailure paymentFailure = (paymentEntity.getFailure() == null)
                ? null
                : PaymentFailure.of(
                paymentEntity.getFailure().reason(),
                paymentEntity.getFailure().failedAt()
        );
        PaymentCancel paymentCancel =(paymentEntity.getCancel() == null)
                ? null
                : PaymentCancel.of(
                paymentEntity.getCancel().reason(),
                paymentEntity.getCancel().cancelAmount(),
                paymentEntity.getCancel().cancelledAt(),
                paymentEntity.getCancel().pgCancelTransactionId()
        );
        PaymentRefund paymentRefund = (paymentEntity.getRefund() == null)
                ? null
                : PaymentRefund.of(
                paymentEntity.getRefund().reason(),
                paymentEntity.getRefund().refundAmount(),
                paymentEntity.getRefund().refundedAt()
        );
        return Payment.reconstruct(
                paymentEntity.getId(),
                paymentEntity.getIdempotencyKey(),
                paymentEntity.getUserId(),
                paymentEntity.getAmount(),
                paymentEntity.getPaymentMethod(),
                paymentEntity.getStatus(),
                paymentEntity.getApprovedAt(),
                paymentEntity.getCardCompany(),
                paymentEntity.getCardNumber(),
                paymentEntity.getInstallmentMonths(),
                paymentEntity.isInterestFree(),
                paymentEntity.getCardType(),
                paymentEntity.getOwnerType(),
                paymentEntity.getCardCompanyCode(),
                paymentEntity.getAcquirerCode(),
                paymentFailure,
                paymentCancel,
                paymentRefund,
                paymentEntity.getPgProvider(),
                paymentEntity.getPgOrderId(),
                paymentEntity.getPgTransactionKey(),
                paymentEntity.getPgStatus(),
                paymentEntity.getPgMessage()
        );
    }
}
