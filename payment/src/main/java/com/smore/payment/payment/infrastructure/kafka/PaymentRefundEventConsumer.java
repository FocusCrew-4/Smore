package com.smore.payment.payment.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.payment.payment.application.PaymentRefundService;
import com.smore.payment.payment.application.event.inbound.PaymentRefundEvent;
import com.smore.payment.payment.application.event.outbound.PaymentRefundFailedEvent;
import com.smore.payment.payment.application.port.in.RefundPaymentUseCase;
import com.smore.payment.payment.application.port.out.OutboxPort;
import com.smore.payment.payment.infrastructure.kafka.dto.PaymentRefundRequestEvent;
import com.smore.payment.payment.infrastructure.persistence.inbox.RefundInbox;
import com.smore.payment.payment.infrastructure.persistence.inbox.RefundInboxProcessor;
import com.smore.payment.payment.infrastructure.persistence.inbox.RefundInboxRepository;
import com.smore.payment.shared.outbox.OutboxMessageCreator;
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
    private final RefundInboxRepository refundInboxRepository;
    private final OutboxPort outboxPort;
    private final OutboxMessageCreator outboxMessageCreator;

    private static final Integer MAX_RETRY = 3;

    @KafkaListener(topics = "order.refund.v1")
    public void handle(String message, Acknowledgment ack) throws JsonProcessingException {
        PaymentRefundRequestEvent event = objectMapper.readValue(message, PaymentRefundRequestEvent.class);

        log.info("PaymentRefundRequestEvent 수신: orderId={}, amount={}",
                event.getOrderId(), event.getRefundAmount());

        RefundInbox inbox = refundInboxRepository
                .findByRefundId(event.getRefundId())
                .orElseThrow();

        if (inbox.getRetryCount() >= MAX_RETRY) {
            log.error("재시도 횟수 한계. refundId={}", event.getRefundId());

            publishRefundDlt(event, inbox);
            ack.acknowledge();
            return;
        }

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

    private void publishRefundDlt(PaymentRefundRequestEvent event, RefundInbox inbox) {

        String reason = String.format(
                "환불 자동 재시도 한계 초과 (retryCount=%d)",
                inbox.getRetryCount()
        );


        outboxPort.save(
                outboxMessageCreator.refundDlt(
                        PaymentRefundFailedEvent.of(
                                event.getOrderId(),
                                event.getRefundId(),
                                BigDecimal.valueOf(event.getRefundAmount()),
                                reason
                        )
                )
        );

        log.error("Refund moved to DLT. refundId={}, reason={}",
                event.getRefundId(), reason);
    }
}
