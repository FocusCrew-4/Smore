package com.smore.payment.payment.application;

import com.smore.payment.payment.application.event.inbound.PaymentRequestedEvent;
import com.smore.payment.payment.application.port.out.TemporaryPaymentPort;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateTemporaryPaymentService {

    private final TemporaryPaymentPort temporaryPaymentPort;
    private final PaymentAuditLogService paymentAuditLogService;

    public void create(PaymentRequestedEvent paymentRequestedEvent) {

        if (temporaryPaymentPort.existsByOrderId(paymentRequestedEvent.orderId())) {
            return;
        }

        TemporaryPayment temp = TemporaryPayment.create(
                paymentRequestedEvent.idempotencyKey(),
                paymentRequestedEvent.orderId(),
                paymentRequestedEvent.userId(),
                paymentRequestedEvent.amount(),
                paymentRequestedEvent.sellerId(),
                paymentRequestedEvent.categoryId(),
                paymentRequestedEvent.auctionType(),
                paymentRequestedEvent.expiredAt()
        );

        temporaryPaymentPort.save(temp);
        paymentAuditLogService.logTemporaryPaymentCreated(paymentRequestedEvent);
    }
}
