package com.smore.payment.payment.infrastructure.kafka;

import com.smore.payment.payment.domain.event.PaymentRequestedEvent;
import com.smore.payment.payment.application.CreateTemporaryPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestedEventConsumer {

    private final CreateTemporaryPaymentService createTemporaryPaymentService;

    @KafkaListener(topics = "payment.requested")
    public void handle(PaymentRequestedEvent event) {

        log.info("PaymentRequestedEvent 수신: orderId={}, amount={}",
                event.getOrderId(), event.getAmount());

        createTemporaryPaymentService.create(event);
    }
}
