package com.smore.payment.payment.application;

import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundSucceededEvent;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.application.port.out.PaymentRepository;
import com.smore.payment.payment.domain.model.Payment;
import com.smore.payment.payment.domain.model.PaymentStatus;
import com.smore.payment.payment.domain.model.PgResponseResult;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundFinalizeService {

    private final PaymentRepository paymentRepository;
    private final OutboxPort outboxPort;
    private final OutboxMessageCreator outboxCreator;

    public void finalizeRefund(PgResponseResult pgResponseResult, PaymentRefundEvent paymentRefundEvent) {

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

        outboxPort.save(
                outboxCreator.paymentRefunded(
                        PaymentRefundSucceededEvent.of(paymentRefundEvent.orderId(), paymentRefundEvent.refundId(), paymentRefundEvent.refundAmount())
                )
        );
    }
}
