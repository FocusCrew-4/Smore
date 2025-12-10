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
                payment.getCancel().cancelledAt()
        );

        PaymentRefundJpa refundJpa = (payment.getRefund() == null)
                ? null
                : PaymentRefundJpa.of(
                payment.getRefund().reason(),
                payment.getRefund().cancelAmount(),
                payment.getRefund().refundedAt(),
                payment.getRefund().pgCancelTransactionKey(),
                payment.getRefund().refundableAmount()
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

                // 카드 정보
                payment.getCardIssuerCode(),
                payment.getCardAcquirerCode(),
                payment.getCardNumber(),
                payment.getInstallmentPlanMonths(),
                payment.isInterestFree(),
                payment.getApproveNo(),
                payment.getCardType(),
                payment.getCardOwnerType(),
                payment.getCardAcquireStatus(),
                payment.getCardAmount(),

                failureJpa,
                cancelJpa,
                refundJpa,

                payment.getPgProvider(),
                payment.getPaymentKey(),
                payment.getPgOrderId(),
                payment.getPgOrderName(),
                payment.getPgTransactionKey(),
                payment.getPgStatus(),
                payment.getCurrency(),
                payment.getPgTotalAmount(),
                payment.getPgBalanceAmount(),

                payment.getSellerId(),
                payment.getCategoryId(),
                payment.getAuctionType()
        );
    }

    public Payment toDomainEntity(PaymentEntity paymentEntity) {
        PaymentFailure failure = (paymentEntity.getFailure() == null)
                ? null
                : PaymentFailure.of(
                paymentEntity.getFailure().reason(),
                paymentEntity.getFailure().failedAt()
        );

        PaymentCancel cancel = (paymentEntity.getCancel() == null)
                ? null
                : PaymentCancel.of(
                paymentEntity.getCancel().reason(),
                paymentEntity.getCancel().cancelAmount(),
                paymentEntity.getCancel().cancelledAt()
        );

        PaymentRefund refund = (paymentEntity.getRefund() == null)
                ? null
                : PaymentRefund.of(
                paymentEntity.getRefund().reason(),
                paymentEntity.getRefund().refundAmount(),
                paymentEntity.getRefund().refundedAt(),
                paymentEntity.getRefund().pgCancelTransactionKey(),
                paymentEntity.getRefund().refundableAmount()
        );

        return Payment.reconstruct(
                paymentEntity.getId(),
                paymentEntity.getIdempotencyKey(),
                paymentEntity.getUserId(),
                paymentEntity.getOrderId(),
                paymentEntity.getAmount(),
                paymentEntity.getPaymentMethod(),
                paymentEntity.getStatus(),
                paymentEntity.getApprovedAt(),

                paymentEntity.getCardIssuerCode(),
                paymentEntity.getCardAcquirerCode(),
                paymentEntity.getCardNumber(),
                paymentEntity.getInstallmentMonths(),
                paymentEntity.getInterestFree(),
                paymentEntity.getApproveNo(),
                paymentEntity.getCardType(),
                paymentEntity.getCardOwnerType(),
                paymentEntity.getCardAcquireStatus(),
                paymentEntity.getCardAmount(),

                failure,
                cancel,
                refund,

                paymentEntity.getPgProvider(),
                paymentEntity.getPaymentKey(),
                paymentEntity.getPgOrderId(),
                paymentEntity.getPgOrderName(),
                paymentEntity.getPgTransactionKey(),
                paymentEntity.getPgStatus(),
                paymentEntity.getCurrency(),
                paymentEntity.getPgTotalAmount(),
                paymentEntity.getPgBalanceAmount(),

                paymentEntity.getSellerId(),
                paymentEntity.getCategoryId(),
                paymentEntity.getAuctionType()
        );
    }
}
