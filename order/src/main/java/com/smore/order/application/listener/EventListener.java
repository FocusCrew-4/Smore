package com.smore.order.application.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.order.application.dto.CompletedRefundCommand;
import com.smore.order.application.dto.CreateOrderCommand;
import com.smore.order.application.dto.FailedRefundCommand;
import com.smore.order.application.service.OrderService;
import com.smore.order.application.event.inbound.BidWinnerConfirmedEvent;
import com.smore.order.application.event.inbound.PaymentCompletedEvent;
import com.smore.order.application.event.inbound.PaymentRefundSucceededEvent;
import com.smore.order.application.event.inbound.PaymentRefundFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j(topic = "EventListener")
@Component
@RequiredArgsConstructor
public class EventListener {

    private final OrderService service;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${topic.bid.order.request}",
        groupId = "${consumer.group.bid}",
        concurrency = "3"
    )
    public void bidWinnerConfirmed(String message, Acknowledgment ack) {
        try {

            BidWinnerConfirmedEvent event = objectMapper.readValue(message,
                BidWinnerConfirmedEvent.class);

            CreateOrderCommand command = CreateOrderCommand.create(
                event.getUserId(),
                event.getProductId(),
                event.getProductPrice(),
                event.getQuantity(),
                event.getIdempotencyKey(),
                event.getExpiresAt(),
                event.getStreet(),
                event.getCity(),
                event.getZipcode()
            );

            service.createOrder(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("BidRequest 처리 실패 : {}", message, e);
        }
    }

    @KafkaListener(
        topics = "${topic.payment.completed}",
        groupId = "${consumer.group.payment}",
        concurrency = "3"
    )
    public void paymentCompleted(String message, Acknowledgment ack) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message,
                PaymentCompletedEvent.class);

            service.completeOrder(event.getOrderId());

            ack.acknowledge();
        } catch (Exception e) {
            log.error("PaymentCompleted 처리 실패 : {}", message, e);
        }
    }

    @KafkaListener(
        topics = "${topic.payment.refund-completed}",
        groupId = "${consumer.group.payment}",
        concurrency = "3"
    )
    public void paymentRefundSucceeded(String message, Acknowledgment ack) {
        try {
            PaymentRefundSucceededEvent event = objectMapper.readValue(message,
                PaymentRefundSucceededEvent.class);

            CompletedRefundCommand command = CompletedRefundCommand.of(
                event.getOrderId(),
                event.getRefundId(),
                event.getRefundAmount()
            );

            service.refundSuccess(command);

            ack.acknowledge();
        } catch (Exception e) {
            // TODO: DLQ/백오프 처리 추가하여 동일 메시지 무한 재시도 방지
            log.error("refundCompleted 처리 실패 : {}", message, e);
        }
    }

    @KafkaListener(
        topics = "${topic.payment.refund-failed}",
        groupId = "${consumer.group.payment}",
        concurrency = "3"
    )
    public void paymentRefundFailed(String message, Acknowledgment ack) {
        try {
            PaymentRefundFailedEvent event = objectMapper.readValue(message,
                PaymentRefundFailedEvent.class);

            FailedRefundCommand command = FailedRefundCommand.of(
                event.getOrderId(),
                event.getRefundId(),
                event.getRefundAmount(),
                event.getMessage()
            );

            service.refundFail(command);

            ack.acknowledge();
        } catch (Exception e) {
            // TODO: DLQ/백오프 처리 추가하여 동일 메시지 무한 재시도 방지
            log.error("refundFailed 처리 실패 : {}", message, e);
        }
    }
}
