package com.smore.payment.payment.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.payment.payment.application.PaymentSettlementService;
import com.smore.payment.payment.application.event.inbound.PaymentSettlementRequestEvent;
import com.smore.payment.payment.application.port.in.SettlePaymentUseCase;
import com.smore.payment.payment.infrastructure.kafka.dto.SettlementRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSettlementEventConsumer {

    private final SettlePaymentUseCase paymentSettlementService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "seller.settlement.v1")
    public void handle(String message, Acknowledgment ack) throws JsonProcessingException {
        SettlementRequestEvent event = objectMapper.readValue(message, SettlementRequestEvent.class);

        log.info("PaymentSettlementRequestEvent 수신");

        PaymentSettlementRequestEvent paymentSettlementRequestEvent = PaymentSettlementRequestEvent.of(
                event.getUserId(),
                event.getAmount(),
                event.getAccountNumber(),
                event.getIdempotencyKey(),
                event.getCreatedAt()
        );

        paymentSettlementService.settle(paymentSettlementRequestEvent);
        ack.acknowledge();
    }
}
