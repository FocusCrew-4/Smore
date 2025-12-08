package com.smore.payment.payment.infrastructure.persistence.mapper;

import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PaymentCancel;
import com.smore.payment.payment.domain.model.PaymentFailure;
import com.smore.payment.payment.domain.model.PaymentRefund;
import com.smore.payment.payment.infrastructure.persistence.model.payment.PaymentCancelJpa;
import com.smore.payment.payment.infrastructure.persistence.model.payment.PaymentEntity;
import com.smore.payment.payment.infrastructure.persistence.model.payment.PaymentFailureJpa;
import com.smore.payment.payment.infrastructure.persistence.model.payment.PaymentRefundJpa;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentEntity toEntity(Payment payment) {

        PaymentFailureJpa failureJpa = (payment.getFailure() == null)
                ? null
                : PaymentFailureJpa.of(
                payment.getFailure().reason(),
                payment.getFailure().failedAt()
        );

        PaymentCancelJpa cancelJpa = (payment.getCancel() == null)
                ? null
                : PaymentCancelJpa.of(
                payment.getCancel().reason(),
                payment.getCancel().cancelAmount(),
                payment.getCancel().cancelledAt(),
                payment.getCancel().pgCancelTransactionId()
        );

        PaymentRefundJpa refundJpa = (payment.getRefund() == null)
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
                payment.getOrderId(),
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
                payment.getAcquirerCode(),

                failureJpa,
                cancelJpa,
                refundJpa,

                payment.getPgProvider(),
                payment.getPgOrderId(),
                payment.getPgTransactionKey(),
                payment.getPgStatus(),
                payment.getPgMessage()
        );
    }
    public Payment toDomainEntity(PaymentEntity e) {

        PaymentFailure failure = (e.getFailure() == null)
                ? null
                : PaymentFailure.of(
                e.getFailure().reason(),
                e.getFailure().failedAt()
        );

        PaymentCancel cancel = (e.getCancel() == null)
                ? null
                : PaymentCancel.of(
                e.getCancel().reason(),
                e.getCancel().cancelAmount(),
                e.getCancel().cancelledAt(),
                e.getCancel().pgCancelTransactionId()
        );

        PaymentRefund refund = (e.getRefund() == null)
                ? null
                : PaymentRefund.of(
                e.getRefund().reason(),
                e.getRefund().refundAmount(),
                e.getRefund().refundedAt()
        );

        return Payment.reconstruct(
                e.getId(),
                e.getIdempotencyKey(),
                e.getUserId(),
                e.getOrderId(),
                e.getAmount(),
                e.getPaymentMethod(),
                e.getStatus(),
                e.getApprovedAt(),

                e.getCardCompany(),
                e.getCardNumber(),
                e.getInstallmentMonths(),
                e.isInterestFree(),
                e.getCardType(),
                e.getOwnerType(),
                e.getAcquirerCode(),

                failure,
                cancel,
                refund,

                e.getPgProvider(),
                e.getPgOrderId(),
                e.getPgTransactionKey(),
                e.getPgStatus(),
                e.getPgMessage()
        );
    }
}

