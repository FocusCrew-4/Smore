package com.smore.payment.payment.application;

import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundFailedEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundSucceededEvent;
import com.smore.payment.payment.application.facade.CancelPolicyFacade;
import com.smore.payment.payment.application.facade.RefundPolicyFacade;
import com.smore.payment.payment.application.facade.dto.CancelPolicyResult;
import com.smore.payment.payment.application.facade.dto.RefundPolicyResult;
import com.smore.payment.payment.application.port.in.RefundPaymentUseCase;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentRefundService implements RefundPaymentUseCase {

    private final PgClient pgClient;

    private final PaymentAuditLogService paymentAuditLogService;

    private final PaymentFinalizeRefund paymentFinalizeRefund;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void refund(PaymentRefundEvent event) {

        paymentAuditLogService.logRefundRequested(event);

        PaymentFinalizeRefund.PolicyResult policy = paymentFinalizeRefund.policyPhaseTx(event);
        if (!policy.refundable()) {
            return;
        }

        paymentFinalizeRefund.markPgRequestedTx(event);

        PgResponseResult pgRefundResult;

        try {
            paymentAuditLogService.logPgRefundRequested(policy.payment(), event, policy.refundAmount());

            pgRefundResult = pgClient.refund(
                    policy.payment().getPaymentKey(),
                    policy.refundAmount(),
                    event.refundReason()
            );

        } catch (RuntimeException e) {
            paymentAuditLogService.logPgRefundFailed(policy.payment(), event, e.getMessage());

            paymentFinalizeRefund.failSystemTx(event, "PG 환불 요청 실패(시스템 오류): " + e.getMessage(), true);

            throw e;
        }

        if (!pgRefundResult.pgStatus().equals("CANCELED")) {
            paymentAuditLogService.logPgRefundFailed(policy.payment(), event, pgRefundResult.failureMessage());

            paymentFinalizeRefund.failSystemTx(event, "PG 환불 상태 오류: " + pgRefundResult.failureMessage(), false);
            return;
        }

        paymentAuditLogService.logPgRefundSucceeded(policy.payment(), event, pgRefundResult);

        try {
            paymentFinalizeRefund.finalizeRefund(pgRefundResult, event);

            paymentAuditLogService.logRefundSucceeded(policy.payment(), event);
        } catch (RuntimeException e) {
            paymentAuditLogService.logRefundFailed(policy.payment(), event, e.getMessage());

            paymentFinalizeRefund.failSystemTx(event, "finalize 실패: " + e.getMessage(), true);
            throw e;
        }
    }


}
