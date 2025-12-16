package com.smore.payment.payment.domain.model;

import com.smore.payment.payment.domain.event.PaymentApprovedEvent;
import com.smore.payment.payment.domain.event.SettlementCalculatedEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    // --- 필드 ---
    private final UUID id;
    private final UUID idempotencyKey;
    private final Long userId;
    private final UUID orderId;
    private final BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime approvedAt;

    // 카드 정보
    private String cardIssuerCode;
    private String cardAcquirerCode;
    private String cardNumber;
    private Integer installmentPlanMonths;
    private boolean interestFree;
    private String approveNo;
    private String cardType;
    private String cardOwnerType;
    private String cardAcquireStatus;
    private BigDecimal cardAmount;

    // 실패 / 취소 / 환불 정보
    private PaymentFailure failure;
    private PaymentCancel cancel;
    private PaymentRefund refund;

    // PG 정보
    private String pgProvider;
    private String paymentKey;
    private String pgOrderId;
    private String pgOrderName;
    private String pgTransactionKey;
    private String pgStatus;
    private String currency;
    private BigDecimal pgTotalAmount;
    private BigDecimal pgBalanceAmount;

    // 정산/정책 정보
    private final Long sellerId;
    private final UUID categoryId;
    private final String auctionType;

    // --- 생성자 (최소 필수 정보) ---
    protected Payment(
            UUID idempotencyKey,
            Long userId,
            UUID orderId,
            BigDecimal amount,
            Long sellerId,
            UUID categoryId,
            String auctionType
    ) {
        this.id = UUID.randomUUID();
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.auctionType = auctionType;
    }

    // --- 결제 승인 완료 후 생성 팩토리 메소드 ---
    public static Payment createFinalPayment(
            UUID idempotencyKey,
            Long userId,
            UUID orderId,
            BigDecimal amount,
            Long sellerId,
            UUID categoryId,
            String auctionType,
            PgResponseResult result
    ) {
        Payment payment = new Payment(
                idempotencyKey,
                userId,
                orderId,
                amount,
                sellerId,
                categoryId,
                auctionType
        );

        // PG 정보 설정
        payment.pgProvider = result.pgProvider();
        payment.paymentKey = result.paymentKey();
        payment.pgTransactionKey = result.transactionKey();
        payment.pgStatus = result.pgStatus();
        payment.pgOrderId = result.pgOrderId();
        payment.pgOrderName = result.pgOrderName();
        payment.currency = result.currency();
        payment.pgTotalAmount = result.totalAmount();
        payment.pgBalanceAmount = result.balanceAmount();

        // 카드 정보 (카드 결제인 경우)
        payment.paymentMethod = PaymentMethod.CARD;
        payment.cardIssuerCode = result.cardIssuerCode();
        payment.cardAcquirerCode = result.cardAcquirerCode();
        payment.cardNumber = result.cardNumber();
        payment.installmentPlanMonths = result.installmentPlanMonths();
        payment.interestFree = result.interestFree();
        payment.approveNo = result.approveNo();
        payment.cardType = result.cardType();
        payment.cardOwnerType = result.cardOwnerType();
        payment.cardAcquireStatus = result.cardAcquireStatus();
        payment.cardAmount = result.cardAmount();

        payment.approvedAt = result.approvedAt();

        payment.status = PaymentStatus.APPROVED;

        // 필요하다면 failure, cancel, refund 는 null 유지

        return payment;
    }

    private Payment(
            UUID id,
            UUID idempotencyKey,
            Long userId,
            UUID orderId,
            BigDecimal amount,
            Long sellerId,
            UUID categoryId,
            String auctionType
    ) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.auctionType = auctionType;
    }

    // --- DB 복원 / 리빌드용 생성자 ---
    public static Payment reconstruct(
            UUID id,
            UUID idempotencyKey,
            Long userId,
            UUID orderId,
            BigDecimal amount,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            LocalDateTime approvedAt,
            String cardIssuerCode,
            String cardAcquirerCode,
            String cardNumber,
            Integer installmentPlanMonths,
            boolean interestFree,
            String approveNo,
            String cardType,
            String cardOwnerType,
            String cardAcquireStatus,
            BigDecimal cardAmount,
            PaymentFailure failure,
            PaymentCancel cancel,
            PaymentRefund refund,
            String pgProvider,
            String paymentKey,
            String pgOrderId,
            String pgOrderName,
            String pgTransactionKey,
            String pgStatus,
            String currency,
            BigDecimal pgTotalAmount,
            BigDecimal pgBalanceAmount,
            Long sellerId,
            UUID categoryId,
            String auctionType
    ) {
        Payment payment = new Payment(
                id, idempotencyKey, userId, orderId, amount, sellerId, categoryId, auctionType
        );

        payment.paymentMethod = paymentMethod;
        payment.status = status;
        payment.approvedAt = approvedAt;

        payment.cardIssuerCode = cardIssuerCode;
        payment.cardAcquirerCode = cardAcquirerCode;
        payment.cardNumber = cardNumber;
        payment.installmentPlanMonths = installmentPlanMonths;
        payment.interestFree = interestFree;
        payment.approveNo = approveNo;
        payment.cardType = cardType;
        payment.cardOwnerType = cardOwnerType;
        payment.cardAcquireStatus = cardAcquireStatus;
        payment.cardAmount = cardAmount;

        payment.failure = failure;
        payment.cancel = cancel;
        payment.refund = refund;

        payment.pgProvider = pgProvider;
        payment.paymentKey = paymentKey;
        payment.pgOrderId = pgOrderId;
        payment.pgOrderName = pgOrderName;
        payment.pgTransactionKey = pgTransactionKey;
        payment.pgStatus = pgStatus;
        payment.currency = currency;
        payment.pgTotalAmount = pgTotalAmount;
        payment.pgBalanceAmount = pgBalanceAmount;

        return payment;
    }

    // --- getters (필요한 것들만 예시) ---
    public UUID getId() { return id; }
    public UUID getIdempotencyKey() { return idempotencyKey; }
    public Long getUserId() { return userId; }
    public UUID getOrderId() { return orderId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getApprovedAt() { return approvedAt; }

    public String getCardIssuerCode() { return cardIssuerCode; }
    public String getCardAcquirerCode() { return cardAcquirerCode; }
    public String getCardNumber() { return cardNumber; }
    public Integer getInstallmentPlanMonths() { return installmentPlanMonths; }
    public boolean isInterestFree() { return interestFree; }
    public String getApproveNo() { return approveNo; }
    public String getCardType() { return cardType; }
    public String getCardOwnerType() { return cardOwnerType; }
    public String getCardAcquireStatus() { return cardAcquireStatus; }
    public BigDecimal getCardAmount() { return cardAmount; }

    public String getPgProvider() { return pgProvider; }
    public String getPaymentKey() { return paymentKey; }
    public String getPgOrderId() { return pgOrderId; }
    public String getPgOrderName() { return pgOrderName; }
    public String getPgTransactionKey() { return pgTransactionKey; }
    public String getPgStatus() { return pgStatus; }
    public String getCurrency() { return currency; }
    public BigDecimal getPgTotalAmount() { return pgTotalAmount; }
    public BigDecimal getPgBalanceAmount() { return pgBalanceAmount; }

    public PaymentFailure getFailure() { return failure; }
    public PaymentCancel getCancel() { return cancel; }
    public PaymentRefund getRefund() { return refund; }

    public Long getSellerId() { return sellerId; }
    public UUID getCategoryId() { return categoryId; }
    public String getAuctionType() { return auctionType; }

    public void updateRefund(String reason, BigDecimal refundAmount, LocalDateTime refundedAt, String cancelTransactionKey, BigDecimal refundableAmount) {
        this.refund = new PaymentRefund(reason, refundAmount, refundedAt, cancelTransactionKey, refundableAmount);
    }

    public PaymentApprovedEvent createApprovedEvent() {
        return new PaymentApprovedEvent(
                orderId,
                id,
                amount,
                approvedAt,
                idempotencyKey
        );
    }

    public SettlementCalculatedEvent createSettlementCalculatedEvent(BigDecimal settlementAmount) {
        return new SettlementCalculatedEvent(
                sellerId,
                settlementAmount,
                idempotencyKey
        );
    }
}
