package com.smore.order.application.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.order.application.dto.CompletedRefundCommand;
import com.smore.order.application.dto.CreateOrderCommand;
import com.smore.order.application.dto.FailedRefundCommand;
import com.smore.order.application.service.OrderService;
import com.smore.order.domain.event.BidRequestEvent;
import com.smore.order.domain.event.CompletedPaymentEvent;
import com.smore.order.domain.event.CompletedRefundEvent;
import com.smore.order.domain.event.FailedRefundEvent;
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
    public void bidRequest(String message, Acknowledgment ack) {
        try {

            BidRequestEvent event = objectMapper.readValue(message,
                BidRequestEvent.class);

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
            CompletedPaymentEvent event = objectMapper.readValue(message,
                CompletedPaymentEvent.class);

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
    public void refundCompleted(String message, Acknowledgment ack) {
        try {
            CompletedRefundEvent event = objectMapper.readValue(message,
                CompletedRefundEvent.class);

            CompletedRefundCommand command = CompletedRefundCommand.of(
                event.getOrderId(),
                event.getRefundId(),
                event.getRefundAmount()
            );

            service.refundSuccess(command);

            ack.acknowledge();
        } catch (Exception e) {
            // TODO: DLQ/알람 처리 등 재시도 한계 초과 시 후속 조치 필요
            log.error("refundCompleted 처리 실패 : {}", message, e);
        }
    }

    @KafkaListener(
        topics = "${topic.payment.refund-failed}",
        groupId = "${consumer.group.payment}",
        concurrency = "3"
    )
    public void refundFailed(String message, Acknowledgment ack) {
        try {
            FailedRefundEvent event = objectMapper.readValue(message,
                FailedRefundEvent.class);

            FailedRefundCommand command = FailedRefundCommand.of(
                event.getOrderId(),
                event.getRefundId(),
                event.getRefundAmount(),
                event.getMessage()
            );

            service.refundFail(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("refundFailed 처리 실패 : {}", message, e);
        }
    }
}
