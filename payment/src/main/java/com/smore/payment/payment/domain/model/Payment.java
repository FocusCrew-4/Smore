package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    // 결제 정보
    private final UUID id;
    private final UUID idempotencyKey;
    private final Long userId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime approvedAt;

    // 카드 정보
    private String cardCompany;
    private String cardNumber;
    private Integer installmentMonths;
    private boolean interestFree;
    private String cardType;
    private String ownerType;
    private String cardCompanyCode;
    private String acquirerCode;

    // 실패 / 취소 / 환불
    private PaymentFailure failure;
    private PaymentCancel cancel;
    private PaymentRefund refund;

    // PG 정보
    private String pgProvider;
    private String pgOrderId;
    private String pgTransactionKey;
    private String pgStatus;
    private String pgMessage;

    protected Payment(
            UUID idempotencyKey,
            Long userId,
            BigDecimal amount,
            PaymentStatus status,
            PaymentMethod paymentMethod
    ) {
        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }


    public static Payment create(UUID idempotencyKey, Long userId, BigDecimal amount, PaymentMethod paymentMethod) {
        return new Payment(
                idempotencyKey,
                userId,
                amount,
                PaymentStatus.REQUESTED,
                paymentMethod
        );
    }

    private Payment(
            UUID id,
            UUID idempotencyKey,
            Long userId,
            BigDecimal amount,
            PaymentStatus status,
            PaymentMethod paymentMethod
    ) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public static Payment reconstruct(
            UUID id,
            UUID idempotencyKey,
            Long userId,
            BigDecimal amount,
            PaymentStatus status,
            PaymentMethod paymentMethod,
            LocalDateTime approvedAt,
            String cardCompany,
            String cardNumber,
            Integer installmentMonths,
            boolean interestFree,
            String cardType,
            String ownerType,
            String cardCompanyCode,
            String acquirerCode,
            PaymentFailure failure,
            PaymentCancel cancel,
            PaymentRefund refund,
            String pgProvider,
            String pgOrderId,
            String pgTransactionKey,
            String pgStatus,
            String pgMessage
    ) {
        Payment payment = new Payment(id, idempotencyKey, userId, amount, status, paymentMethod);
        payment.approvedAt = approvedAt;
        payment.cardCompany = cardCompany;
        payment.cardNumber = cardNumber;
        payment.installmentMonths = installmentMonths;
        payment.interestFree = interestFree;
        payment.cardType = cardType;
        payment.ownerType = ownerType;
        payment.cardCompanyCode = cardCompanyCode;
        payment.acquirerCode = acquirerCode;

        payment.failure = failure;
        payment.cancel = cancel;
        payment.refund = refund;

        payment.pgProvider = pgProvider;
        payment.pgOrderId = pgOrderId;
        payment.pgTransactionKey = pgTransactionKey;
        payment.pgStatus = pgStatus;
        payment.pgMessage = pgMessage;

        return payment;
    }

}
