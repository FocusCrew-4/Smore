package com.smore.payment.payment.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.payment.payment.application.PaymentRefundService;
import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.event.inbound.PaymentRequestedEvent;
import com.smore.payment.payment.infrastructure.kafka.dto.PaymentCreateRequestEvent;
import com.smore.payment.payment.infrastructure.kafka.dto.PaymentRefundRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRefundEventConsumer {

    private final PaymentRefundService paymentRefundService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "order.refund.v1")
    public void handle(String message, Acknowledgment ack) throws JsonProcessingException {
        PaymentRefundRequestEvent event = objectMapper.readValue(message, PaymentRefundRequestEvent.class);

        log.info("PaymentRefundRequestEvent 수신: orderId={}, amount={}",
                event.getOrderId(), event.getRefundAmount());

        PaymentRefundEvent paymentRequestedEvent = PaymentRefundEvent.of(
                event.getOrderId(),
                event.getUserId(),
                event.getRefundId(),
                event.getPaymentId(),
                BigDecimal.valueOf(event.getRefundAmount()),
                event.getIdempotencyKey(),
                event.getReason(),
                event.getPublishedAt()
        );

        // 만료되지 않았으면 정상 처리
        paymentRefundService.refund(paymentRequestedEvent);
        ack.acknowledge();
    }
}
