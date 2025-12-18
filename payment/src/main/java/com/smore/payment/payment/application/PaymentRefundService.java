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
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.service.RefundCalculator;
import com.smore.payment.payment.domain.service.RefundDecision;
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

    private final PaymentRepository paymentRepository;
    private final CancelPolicyFacade cancelPolicyFacade;
    private final RefundPolicyFacade refundPolicyFacade;
    private final PgClient pgClient;
    private final OutboxMessageCreator outboxCreator;
    private final OutboxPort outboxPort;
    private final RefundCalculator refundCalculator;
    private final PaymentAuditLogService paymentAuditLogService;
    private final RefundFinalizeService refundFinalizeService;

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void refund(PaymentRefundEvent event) {

        paymentAuditLogService.logRefundRequested(event);

        Payment payment = paymentRepository.findById(event.paymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. paymentId=" + event.paymentId()));

        CancelPolicyResult cancelResult = cancelPolicyFacade.findApplicablePolicy(
                payment.getSellerId(),
                payment.getCategoryId(),
                payment.getAuctionType()
        );

        RefundPolicyResult refundResult = refundPolicyFacade.findApplicablePolicy(
                payment.getSellerId(),
                payment.getCategoryId(),
                payment.getAuctionType()
        );


        RefundDecision refundDecision = refundCalculator.decide(payment, event, cancelResult, refundResult);
        if (!refundDecision.refundable()) {
            paymentAuditLogService.logRefundFailed(payment, event, refundDecision.failureReason());

            OutboxMessage failedMsg = outboxCreator.paymentRefundFailed(
                    PaymentRefundFailedEvent.of(event.orderId(), event.refundId(), event.refundAmount(), refundDecision.failureReason())
            );
            outboxPort.save(failedMsg);
            return;
        }

        final BigDecimal refundAmount = refundDecision.refundAmount();

        PgResponseResult pgRefundResult;

        try {

            paymentAuditLogService.logPgRefundRequested(payment, event, refundAmount);

            pgRefundResult = pgClient.refund(
                    payment.getPaymentKey(),
                    refundAmount,
                    event.refundReason()
            );

            paymentAuditLogService.logPgRefundSucceeded(payment, event, pgRefundResult);

        } catch (Exception e) {

            paymentAuditLogService.logPgRefundFailed(payment, event, e.getMessage());

            OutboxMessage failedMsg = outboxCreator.paymentRefundFailed(
                    PaymentRefundFailedEvent.of(event.orderId(), event.refundId(), refundDecision.refundAmount(), e.getMessage())
            );
            outboxPort.save(failedMsg);

            throw e;
        }

        refundFinalizeService.finalizeRefund(pgRefundResult, event);

        paymentAuditLogService.logRefundSucceeded(payment, event);
    }

}
