package com.smore.payment.payment.application;

import com.smore.payment.payment.application.port.in.RefundPaymentUseCase;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.domain.service.RefundCalculator;
import com.smore.payment.payment.domain.service.RefundDecision;
import com.smore.payment.shared.outbox.OutboxMessage;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundFailedEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundSucceededEvent;
import com.smore.payment.payment.application.facade.CancelPolicyFacade;
import com.smore.payment.payment.application.facade.RefundPolicyFacade;
import com.smore.payment.payment.application.facade.dto.CancelPolicyResult;
import com.smore.payment.payment.application.facade.dto.RefundPolicyResult;
import com.smore.payment.payment.application.port.out.PgClient;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentRefundService implements RefundPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final CancelPolicyFacade cancelPolicyFacade;
    private final RefundPolicyFacade refundPolicyFacade;
    private final PgClient pgClient;
    private final OutboxMessageCreator outboxCreator;
    private final OutboxPort outboxPort;
    private final RefundCalculator refundCalculator;

    @Override
    public void refund(PaymentRefundEvent event) {

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
            OutboxMessage failedMsg = outboxCreator.paymentRefundFailed(
                    PaymentRefundFailedEvent.of(event.orderId(), event.refundId(), event.refundAmount(), refundDecision.failureReason())
            );
            outboxRepository.save(failedMsg);
            return;
        }

        final BigDecimal refundAmount = refundDecision.refundAmount();

        PgResponseResult pgRefundResult;

        try {
            pgRefundResult = pgClient.refund(
                    payment.getPaymentKey(),
                    refundAmount,
                    event.refundReason()
            );

        } catch (Exception e) {

            OutboxMessage failedMsg = outboxCreator.paymentRefundFailed(
                    PaymentRefundFailedEvent.of(event.orderId(), event.refundId(), refundDecision.refundAmount(), e.getMessage())
            );
            outboxPort.save(failedMsg);

            throw e;
        }

        payment.updateRefund(
                pgRefundResult.cancels().cancelReason(),
                event.refundAmount(),
                pgRefundResult.cancels().canceledAt(),
                pgRefundResult.cancels().cancelTransactionKey(),
                pgRefundResult.cancels().cancelAmount()
        );
        paymentRepository.updateRefund(payment.getId(), payment.getRefund());

        // Todo: 환불 후 정산금 어떻게 하지..........
        OutboxMessage successMsg = outboxCreator.paymentRefunded(
                PaymentRefundSucceededEvent.of(event.orderId(), event.refundId(), refundDecision.refundAmount())
        );
        outboxPort.save(successMsg);
    }
}
