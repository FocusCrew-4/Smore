package com.smore.order.application.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.order.application.dto.CompletedPaymentCommand;
import com.smore.order.application.dto.CompletedRefundCommand;
import com.smore.order.application.dto.CreateOrderCommand;
import com.smore.order.application.dto.FailedOrderCommand;
import com.smore.order.application.dto.FailedRefundCommand;
import com.smore.order.application.dto.RefundCommand;
import com.smore.order.application.event.inbound.BidInventoryConfirmationTimedOutEvent;
import com.smore.order.application.event.inbound.PaymentFailedEvent;
import com.smore.order.application.service.OrderService;
import com.smore.order.application.event.inbound.BidWinnerConfirmedEvent;
import com.smore.order.application.event.inbound.PaymentCompletedEvent;
import com.smore.order.application.event.inbound.PaymentRefundSucceededEvent;
import com.smore.order.application.event.inbound.PaymentRefundFailedEvent;
import com.smore.order.domain.status.RefundTriggerType;
import com.smore.order.domain.status.SaleType;
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
                event.getCategoryId(),
                SaleType.from(event.getSaleType()),
                event.getSellerId(),
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

            CompletedPaymentCommand command = CompletedPaymentCommand.of(
                event.getOrderId(),
                event.getPaymentId(),
                event.getAmount().intValue(), // TODO: 나중에 돈과 관련된 모든 필드는 BigDecimal로 변경될 예정
                event.getApprovedAt()
            );

            service.completeOrder(command);

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

    @KafkaListener(
        topics = "${topic.payment.failed}",
        groupId = "${consumer.group.payment}",
        concurrency = "2"
    )
    public void paymentFailed(String message, Acknowledgment ack) {
        try {
            PaymentFailedEvent event = objectMapper.readValue(message,
                PaymentFailedEvent.class);

            FailedOrderCommand command = FailedOrderCommand.of(
                event.getOrderId(),
                event.getPaymentId(),
                event.getErrorMessage()
            );

            service.failOrder(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("PaymentFailed 처리 실패: {}", message, e);
        }
    }

    @KafkaListener(
        topics = "${topic.bid.order.inventory-confirm-timeout}",
        groupId = "${consumer.group.bid}",
        concurrency = "2"
    )
    public void inventoryConfirmTimeout(String message, Acknowledgment ack) {
        try {
            BidInventoryConfirmationTimedOutEvent event = objectMapper.readValue(message,
                BidInventoryConfirmationTimedOutEvent.class);

            RefundCommand command = RefundCommand.of(
                event.getOrderId(),
                event.getUserId(),
                event.getRefundQuantity(),
                event.getReason(),
                event.getIdempotencyKey(),
                RefundTriggerType.INVENTORY_CONFIRM_TIMEOUT
            );

            service.refund(command);

            ack.acknowledge();
        } catch (Exception e) {
            log.error("inventoryConfirmTimeout 처리 실패: {}", message, e);
        }
    }
}
