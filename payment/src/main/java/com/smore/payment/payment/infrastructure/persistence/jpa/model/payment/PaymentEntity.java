package com.smore.payment.payment.infrastructure.persistence.jpa.model.payment;

import com.smore.payment.payment.domain.model.PaymentMethod;
import com.smore.payment.payment.domain.model.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentEntity {

    @Id
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private UUID idempotencyKey;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // 카드 정보
    @Column(name = "card_issuer_code")
    private String cardIssuerCode;

    @Column(name = "card_acquirer_code")
    private String cardAcquirerCode;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "installment_months")
    private Integer installmentMonths;

    @Column(name = "interest_free")
    private Boolean interestFree;

    @Column(name = "approve_no")
    private String approveNo;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_owner_type")
    private String cardOwnerType;

    @Column(name = "card_acquire_status")
    private String cardAcquireStatus;

    @Column(name = "card_amount")
    private BigDecimal cardAmount;

    // 실패 / 취소 / 환불 정보 — Embeddable로 분리
    @Embedded
    private PaymentFailureJpa failure;

    @Embedded
    private PaymentCancelJpa cancel;

    @Embedded
    @Setter
    private PaymentRefundJpa refund;

    // PG 정보
    @Column(name = "pg_provider")
    private String pgProvider;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "pg_order_id")
    private String pgOrderId;

    @Column(name = "pg_order_name")
    private String pgOrderName;

    @Column(name = "pg_transaction_key")
    private String pgTransactionKey;

    @Column(name = "pg_status")
    private String pgStatus;

    @Column(name = "currency")
    private String currency;

    @Column(name = "pg_total_amount")
    private BigDecimal pgTotalAmount;

    @Column(name = "pg_balance_amount")
    private BigDecimal pgBalanceAmount;

    // 정산/정책 정보
    @Column(name = "seller_id", nullable = false, updatable = false)
    private Long sellerId;

    @Column(name = "category", nullable = false, updatable = false)
    private UUID categoryId;

    @Column(name = "auction_type", nullable = false, updatable = false)
    private String auctionType;

    // 변경 가능한 생성자 또는 빌더 패턴 사용 권장
    public PaymentEntity(UUID id,
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
                         Integer installmentMonths,
                         Boolean interestFree,
                         String approveNo,
                         String cardType,
                         String cardOwnerType,
                         String cardAcquireStatus,
                         BigDecimal cardAmount,
                         PaymentFailureJpa failure,
                         PaymentCancelJpa cancel,
                         PaymentRefundJpa refund,
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
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.approvedAt = approvedAt;

        this.cardIssuerCode = cardIssuerCode;
        this.cardAcquirerCode = cardAcquirerCode;
        this.cardNumber = cardNumber;
        this.installmentMonths = installmentMonths;
        this.interestFree = interestFree;
        this.approveNo = approveNo;
        this.cardType = cardType;
        this.cardOwnerType = cardOwnerType;
        this.cardAcquireStatus = cardAcquireStatus;
        this.cardAmount = cardAmount;

        this.failure = failure;
        this.cancel = cancel;
        this.refund = refund;

        this.pgProvider = pgProvider;
        this.paymentKey = paymentKey;
        this.pgOrderId = pgOrderId;
        this.pgOrderName = pgOrderName;
        this.pgTransactionKey = pgTransactionKey;
        this.pgStatus = pgStatus;
        this.currency = currency;
        this.pgTotalAmount = pgTotalAmount;
        this.pgBalanceAmount = pgBalanceAmount;

        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.auctionType = auctionType;
    }
}
