package com.smore.payment.payment.infrastructure.persistence.model;

import com.smore.payment.payment.domain.model.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentEntity {

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @Id
    private UUID id;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private UUID idempotencyKey;

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "amount", nullable = false, updatable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "card_company")
    private String cardCompany;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "installment_months")
    private Integer installmentMonths;

    @Column(name = "interest_free")
    private boolean interestFree;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "owner_type")
    private String ownerType;

    @Column(name = "card_company_code")
    private String cardCompanyCode;

    @Column(name = "acquirer_code")
    private String acquirerCode;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "reason", column = @Column(name = "failure_reason")),
            @AttributeOverride(name = "failedAt", column = @Column(name = "failed_at"))
    })
    private PaymentFailureJpa failure;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "reason", column = @Column(name = "cancel_reason")),
            @AttributeOverride(name = "cancelAmount", column = @Column(name = "cancel_amount")),
            @AttributeOverride(name = "cancelledAt", column = @Column(name = "cancelled_at")),
            @AttributeOverride(name = "pgCancelTransactionId", column = @Column(name = "pg_cancel_transaction_id"))
    })
    private PaymentCancelJpa cancel;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "reason", column = @Column(name = "refund_reason")),
            @AttributeOverride(name = "refundAmount", column = @Column(name = "refund_amount")),
            @AttributeOverride(name = "refundedAt", column = @Column(name = "refunded_at"))
    })
    private PaymentRefundJpa refund;

    @Column(name = "pg_provider")
    private String pgProvider;

    @Column(name = "pg_order_id")
    private String pgOrderId;

    @Column(name = "pg_transaction_key")
    private String pgTransactionKey;

    @Column(name = "pg_status")
    private String pgStatus;

    @Column(name = "pg_message")
    private String pgMessage;

    public PaymentEntity(
            UUID id,
            UUID idempotencyKey,
            Long userId,
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
            String cardCompanyCode,
            String acquirerCode,
            PaymentFailureJpa failure,
            PaymentCancelJpa cancel,
            PaymentRefundJpa refund,
            String pgProvider,
            String pgOrderId,
            String pgTransactionKey,
            String pgStatus,
            String pgMessage
    ) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.approvedAt = approvedAt;
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
        this.installmentMonths = installmentMonths;
        this.interestFree = interestFree;
        this.cardType = cardType;
        this.ownerType = ownerType;
        this.cardCompanyCode = cardCompanyCode;
        this.acquirerCode = acquirerCode;
        this.failure = failure;
        this.cancel = cancel;
        this.refund = refund;
        this.pgProvider = pgProvider;
        this.pgOrderId = pgOrderId;
        this.pgTransactionKey = pgTransactionKey;
        this.pgStatus = pgStatus;
        this.pgMessage = pgMessage;
    }
}
