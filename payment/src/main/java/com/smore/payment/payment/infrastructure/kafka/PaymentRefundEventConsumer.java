package com.smore.payment.payment.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.port.in.RefundPaymentUseCase;
import com.smore.payment.payment.infrastructure.kafka.dto.PaymentRefundRequestEvent;
import com.smore.payment.payment.infrastructure.persistence.inbox.RefundInboxProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRefundEventConsumer {

    private final RefundInboxProcessor refundInboxProcessor;
    private final RefundPaymentUseCase paymentRefundService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "order.refund.v1",
            groupId = "consumer.refund.group",
            concurrency = "3"
    )
    public void handle(String message, Acknowledgment ack) throws JsonProcessingException {
        PaymentRefundRequestEvent event = objectMapper.readValue(message, PaymentRefundRequestEvent.class);

        log.info("PaymentRefundRequestEvent 수신: orderId={}, amount={}",
                event.getOrderId(), event.getRefundAmount());


        boolean start = refundInboxProcessor.startOrSkip(event);
        if (!start) {
            // 이미 완료 된 이벤트 또는 처리 중인 이벤트
            ack.acknowledge();
            return;
        }

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

        paymentRefundService.refund(paymentRequestedEvent);

        ack.acknowledge();
    }


}
