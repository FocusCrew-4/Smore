package com.smore.bidcompetition.application.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.bidcompetition.application.dto.BidCreateCommand;
import com.smore.bidcompetition.application.dto.OrderCompletedCommand;
import com.smore.bidcompetition.application.dto.OrderFailedCommand;
import com.smore.bidcompetition.application.dto.RefundSucceededCommand;
import com.smore.bidcompetition.application.dto.ServiceResult;
import com.smore.bidcompetition.application.service.BidCompetitionService;
import com.smore.bidcompetition.infrastructure.persistence.event.inbound.OrderCompletedEvent;
import com.smore.bidcompetition.infrastructure.persistence.event.inbound.OrderFailedEvent;
import com.smore.bidcompetition.infrastructure.persistence.event.inbound.ProductCreatedEvent;
import com.smore.bidcompetition.infrastructure.persistence.event.inbound.RefundSucceedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j(topic = "EventListener")
@Component
@RequiredArgsConstructor
public class EventListener {

    private final BidCompetitionService service;
    private final ObjectMapper objectMapper;

    @KafkaListener(

        topics = "${topic.product.created}",
        groupId = "${consumer.group.product}",
        concurrency = "2"
    )
    public void productCreated(String message, Acknowledgment ack) {
        try {
            ProductCreatedEvent event = objectMapper.readValue(message,
                ProductCreatedEvent.class);

            BidCreateCommand command = BidCreateCommand.of(
                event.getProductId(),
                event.getCategoryId(),
                event.getSellerId(),
                event.getProductPrice(),
                event.getStock(),
                event.getStartAt(),
                event.getEndAt(),
                event.getIdempotencyKey()
            );

            service.createBid(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("productCreated 처리 실패 : {}", message, e);
        }
    }

    @KafkaListener(
        topics = "${topic.order.completed}",
        groupId = "${consumer.group.order}",
        concurrency = "3"
    )
    public void orderCompleted(String message, Acknowledgment ack) {
        try {
            OrderCompletedEvent event = objectMapper.readValue(message,
                OrderCompletedEvent.class);

            OrderCompletedCommand command = OrderCompletedCommand.of(
                event.getOrderId(),
                event.getUserId(),
                event.getCurrentOrderStatus(),
                event.getIdempotencyKey(),
                event.getPaidAt()
            );

            service.orderCompleted(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("orderCompleted 처리 실패 : {}", message, e);
        }
    }

    @KafkaListener(
        topics = "${topic.order.failed}",
        groupId = "${consumer.group.order}",
        concurrency = "3"
    )
    public void orderFailed(String message, Acknowledgment ack) {
        try {
            OrderFailedEvent event = objectMapper.readValue(message,
                OrderFailedEvent.class);

            OrderFailedCommand command = OrderFailedCommand.of(
                event.getAllocationKey(),
                event.getProductId(),
                event.getUserId(),
                event.getQuantity(),
                event.getPublishedAt()
            );

            service.orderFailed(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("orderFailed 처리 실패 : {}", message, e);
        }
    }

    @KafkaListener(
        topics = "${topic.order.refund-success}",
        groupId = "${consumer.group.order}",
        concurrency = "3"
    )
    public void refundSucceedEvent(String message, Acknowledgment ack) {
        try {

            RefundSucceedEvent event = objectMapper.readValue(message,
                RefundSucceedEvent.class);

            RefundSucceededCommand command = RefundSucceededCommand.of(
                event.getOrderId(),
                event.getRefundId(),
                event.getUserId(),
                event.getQuantity(),
                event.getAllocationKey(),
                event.getRefundAmount(),
                event.getStatus(),
                event.getPublishedAt()
            );

            service.refundSuccess(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("refundSucceedEvent 처리 실패 : {}", message, e);
        }

    }
}
