package com.smore.payment.payment.application;

import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundFailedEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundSucceededEvent;
import com.smore.payment.payment.application.facade.CancelPolicyFacade;
import com.smore.payment.payment.application.facade.RefundPolicyFacade;
import com.smore.payment.payment.application.facade.dto.CancelPolicyResult;
import com.smore.payment.payment.application.facade.dto.RefundPolicyResult;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.application.port.out.PaymentRepository;
import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PaymentStatus;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.service.RefundCalculator;
import com.smore.payment.payment.domain.service.RefundDecision;
import com.smore.payment.payment.infrastructure.persistence.inbox.RefundInbox;
import com.smore.payment.payment.infrastructure.persistence.inbox.RefundInboxRepository;
import com.smore.payment.shared.outbox.OutboxMessage;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentFinalizeRefund {

    private final RefundInboxRepository refundInboxRepository;

    private final PaymentRepository paymentRepository;
    private final CancelPolicyFacade cancelPolicyFacade;
    private final RefundPolicyFacade refundPolicyFacade;
    private final RefundCalculator refundCalculator;

    private final OutboxMessageCreator outboxCreator;
    private final OutboxPort outboxPort;
    private final PaymentAuditLogService paymentAuditLogService;

    private static final Long MAX_RETRY_COUNT = 3L;

    /**
     * 1) 정책 판단 + Inbox 상태 갱신 (짧은 TX)
     */
    @Transactional
    public PolicyResult policyPhaseTx(PaymentRefundEvent event) {
        RefundInbox inbox = refundInboxRepository.findByRefundId(event.refundId())
                .orElseThrow(() -> new IllegalStateException("inbox not found. refundId=" + event.refundId()));

        if (inbox.isFinalized()) {
            return new PolicyResult(false, BigDecimal.ZERO, "이미 FINALIZED", null);
        }

        Payment payment = paymentRepository.findById(event.paymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. paymentId=" + event.paymentId()));

        CancelPolicyResult cancelResult = cancelPolicyFacade.findApplicablePolicy(
                payment.getSellerId(), payment.getCategoryId(), payment.getAuctionType()
        );

        RefundPolicyResult refundResult = refundPolicyFacade.findApplicablePolicy(
                payment.getSellerId(), payment.getCategoryId(), payment.getAuctionType()
        );

        RefundDecision decision = refundCalculator.decide(payment, event, cancelResult, refundResult);

        if (!decision.refundable()) {
            paymentAuditLogService.logRefundFailed(payment, event, decision.failureReason());

            inbox.markFailed(decision.failureReason());
            refundInboxRepository.save(inbox);

            // 정책 불가 실패는 “재시도 대상 아님”이 일반적
            OutboxMessage failedMsg = outboxCreator.paymentRefundFailed(
                    PaymentRefundFailedEvent.of(event.orderId(), event.refundId(), event.refundAmount(), decision.failureReason())
            );
            outboxPort.save(failedMsg);

            return new PolicyResult(false, BigDecimal.ZERO, decision.failureReason(), payment);
        }

        inbox.markPolicyPassed();
        refundInboxRepository.save(inbox);

        return new PolicyResult(true, decision.refundAmount(), null, payment);
    }

    /**
     * 2) PG 요청 전 상태 기록
     */
    @Transactional
    public void markPgRequestedTx(PaymentRefundEvent event) {
        RefundInbox inbox = refundInboxRepository.findByRefundId(event.refundId())
                .orElseThrow();
        if (inbox.isFinalized()) return;

        inbox.markPgRequested();
        refundInboxRepository.save(inbox);
    }

    /**
     * PG 실패/시스템 실패 등 재시도 고려 케이스
     */
    @Transactional
    public void failSystemTx(PaymentRefundEvent event, String reason, boolean toDlt) {
        RefundInbox inbox = refundInboxRepository.findByRefundId(event.refundId())
                .orElseThrow();

        inbox.increaseRetry(reason);

        boolean retryable = inbox.getRetryCount() <= MAX_RETRY_COUNT;

        if (!retryable) {
            inbox.markFailed(reason);
        }
        refundInboxRepository.save(inbox);

        PaymentRefundFailedEvent payload =
                PaymentRefundFailedEvent.of(
                        event.orderId(),
                        event.refundId(),
                        event.refundAmount(),
                        reason
                );

        OutboxMessage msg;
        if (retryable) {
            msg = outboxCreator.refundRetry(payload);
        } else if (toDlt) {
            msg = outboxCreator.refundDlt(payload);
        } else {
            msg = outboxCreator.paymentRefundFailed(payload);
        }

        outboxPort.save(msg);
    }

    /**
     * 3) PG 성공 반영 + Payment 업데이트 + Outbox + Inbox FINALIZED (정합성 핵심)
     */
    @Transactional
    public void finalizeRefund(PgResponseResult pgResponseResult, PaymentRefundEvent paymentRefundEvent) {
        RefundInbox inbox = refundInboxRepository.findByRefundId(paymentRefundEvent.refundId())
                .orElseThrow();

        if (inbox.isFinalized()) {
            return; // 멱등
        }

        inbox.markPgSucceeded(pgResponseResult.transactionKey());
        refundInboxRepository.save(inbox);

        Payment payment = paymentRepository
                .findById(paymentRefundEvent.paymentId())
                .orElseThrow();

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return;
        }

        payment.updateRefund(
                pgResponseResult.cancels().cancelReason(),
                paymentRefundEvent.refundAmount(),
                pgResponseResult.cancels().canceledAt(),
                pgResponseResult.cancels().cancelTransactionKey(),
                pgResponseResult.cancels().cancelAmount(),
                "REFUNDED"
        );
        paymentRepository.updateRefund(payment.getId(), payment.getRefund());

        inbox.markFinalized();
        refundInboxRepository.save(inbox);

        outboxPort.save(
                outboxCreator.paymentRefunded(
                        PaymentRefundSucceededEvent.of(paymentRefundEvent.orderId(), paymentRefundEvent.refundId(), paymentRefundEvent.refundAmount())
                )
        );
    }

    public record PolicyResult(
            boolean refundable,
            BigDecimal refundAmount,
            String failureReason,
            Payment payment
    ) {
    }
}
