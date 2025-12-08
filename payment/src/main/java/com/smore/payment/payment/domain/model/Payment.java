package com.smore.payment.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    // 결제 정보
    private final UUID id;
    private final UUID idempotencyKey;
    private final Long userId;
    private final UUID orderId;  // ← 추가됨
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
    private String acquirerCode;

    // 실패 / 취소 / 환불 정보
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
            UUID orderId,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            PaymentStatus status
    ) {
        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }
    public static Payment createFinalPayment(
            UUID idempotencyKey,
            Long userId,
            UUID orderId,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            PgApproveResult result)
    {
        Payment payment = new Payment(
                idempotencyKey,
                userId,
                orderId,
                amount,
                paymentMethod,
                PaymentStatus.APPROVED
        );

        payment.approvedAt = result.approvedAt();

        payment.cardCompany = result.cardCompany();
        payment.cardNumber = result.cardNumber();
        payment.installmentMonths = result.installmentMonths();
        payment.interestFree = result.interestFree();
        payment.cardType = result.cardType();
        payment.ownerType = result.ownerType();
        payment.acquirerCode = result.acquirerCode();

        payment.pgProvider = result.pgProvider();
        payment.pgOrderId = result.pgOrderId();
        payment.pgTransactionKey = result.pgTransactionKey();
        payment.pgStatus = result.pgStatus();
        payment.pgMessage = "Success";

        return payment;
    }
    public static Payment create(
            UUID idempotencyKey,
            Long userId,
            UUID orderId,
            BigDecimal amount,
            PaymentMethod paymentMethod
    ) {
        return new Payment(
                idempotencyKey,
                userId,
                orderId,
                amount,
                paymentMethod,
                PaymentStatus.REQUESTED
        );
    }

    private Payment(
            UUID id,
            UUID idempotencyKey,
            Long userId,
            UUID orderId,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            PaymentStatus status
    ) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public static Payment reconstruct(
            UUID id,
            UUID idempotencyKey,
            Long userId,
            UUID orderId,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            LocalDateTime approvedAt,
            String cardCompany,
            String cardNumber,
            Integer installmentMonths,
            boolean interestFree,
            String cardType,
            String ownerType,
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
        Payment payment = new Payment(id, idempotencyKey, userId, orderId, amount, paymentMethod, status);

        payment.approvedAt = approvedAt;
        payment.cardCompany = cardCompany;
        payment.cardNumber = cardNumber;
        payment.installmentMonths = installmentMonths;
        payment.interestFree = interestFree;
        payment.cardType = cardType;
        payment.ownerType = ownerType;
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



    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public UUID getIdempotencyKey() { return idempotencyKey; }
    public Long getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getApprovedAt() { return approvedAt; }

    public String getCardCompany() { return cardCompany; }
    public String getCardNumber() { return cardNumber; }
    public Integer getInstallmentMonths() { return installmentMonths; }
    public boolean isInterestFree() { return interestFree; }
    public String getCardType() { return cardType; }
    public String getOwnerType() { return ownerType; }
    public String getAcquirerCode() { return acquirerCode; }

    public PaymentFailure getFailure() { return failure; }
    public PaymentCancel getCancel() { return cancel; }
    public PaymentRefund getRefund() { return refund; }

    public String getPgProvider() { return pgProvider; }
    public String getPgOrderId() { return pgOrderId; }
    public String getPgTransactionKey() { return pgTransactionKey; }
    public String getPgStatus() { return pgStatus; }
    public String getPgMessage() { return pgMessage; }
}
