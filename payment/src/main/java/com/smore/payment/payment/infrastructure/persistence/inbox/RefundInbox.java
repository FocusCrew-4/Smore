package com.smore.payment.payment.infrastructure.persistence.inbox;

import com.smore.payment.payment.infrastructure.kafka.dto.PaymentRefundRequestEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "refund_inbox",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refund_inbox_refund_id",
                        columnNames = "refundId"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundInbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private UUID refundId;

    @Column(nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private UUID paymentId;

    @Column
    private UUID idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundInboxStatus status;

    @Column(nullable = false)
    private int retryCount;

    private String lastError;

    private String pgRefundKey;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static RefundInbox create(PaymentRefundRequestEvent event) {
        RefundInbox inbox = new RefundInbox();
        inbox.refundId = event.getRefundId();
        inbox.orderId = event.getOrderId();
        inbox.paymentId = event.getPaymentId();
        inbox.idempotencyKey = event.getIdempotencyKey() == null ? null : event.getIdempotencyKey();
        inbox.status = RefundInboxStatus.RECEIVED;
        inbox.retryCount = 0;
        inbox.createdAt = LocalDateTime.now();
        inbox.updatedAt = LocalDateTime.now();
        return inbox;
    }

    public void markProcessing() {
        if (this.status == RefundInboxStatus.FINALIZED) return;
        if (this.status == RefundInboxStatus.PROCESSING) return; // 이미 누가 잡음
        this.status = RefundInboxStatus.PROCESSING;
        setUpdatedAt();
    }

    public void markPolicyPassed() {
        this.status = RefundInboxStatus.POLICY_PASSED;
        setUpdatedAt();
    }

    public void markPgRequested() {
        this.status = RefundInboxStatus.PG_REQUESTED;
        setUpdatedAt();
    }

    public void markPgSucceeded(String pgRefundKey) {
        this.status = RefundInboxStatus.PG_SUCCEEDED;
        this.pgRefundKey = pgRefundKey;
        setUpdatedAt();
    }

    public void markFinalized() {
        this.status = RefundInboxStatus.FINALIZED;
        setUpdatedAt();
    }

    public void markFailed(String error) {
        this.status = RefundInboxStatus.FAILED;
        this.lastError = error;
        setUpdatedAt();
    }

    public void increaseRetry(String error) {
        this.retryCount++;
        this.lastError = error;
        setUpdatedAt();
    }

    public boolean isFinalized() {
        return this.status == RefundInboxStatus.FINALIZED;
    }

    public boolean isProcessing() {
        return this.status == RefundInboxStatus.PROCESSING;
    }

    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
