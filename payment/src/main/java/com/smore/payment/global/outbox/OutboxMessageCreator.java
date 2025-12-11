package com.smore.payment.global.outbox;

import com.smore.payment.global.util.JsonUtil;
import com.smore.payment.payment.application.event.outbound.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxMessageCreator {

    private final JsonUtil jsonUtil;


    @Value("${topic.order.approved}")
    private String orderApprovedTopic;

    @Value("${topic.seller.approved}")
    private String sellerApprovedTopic;

    @Value("${topic.order.failed}")
    private String orderFailedTopic;

    @Value("${topic.order.refund}")
    private String orderRefundTopic;

    @Value("${topic.order.refund-fail}")
    private String orderRefundFailTopic;

    public OutboxMessage paymentApproved(PaymentApprovedEvent event) {
        return new OutboxMessage(
                "PAYMENT",
                event.paymentId(),
                event.getClass().getSimpleName(),
                event.idempotencyKey(),
                orderApprovedTopic,
                jsonUtil.jsonToString(event),
                3,
                OutboxStatus.PENDING
        );
    }

    public OutboxMessage paymentFailed(PaymentFailedEvent event) {
        return new OutboxMessage(
                "PAYMENT",
                event.orderId(),
                event.getClass().getSimpleName(),
                UUID.randomUUID(),
                orderFailedTopic,
                jsonUtil.jsonToString(event),
                3,
                OutboxStatus.PENDING
        );
    }

    public OutboxMessage settlementCalculated(SettlementCalculatedEvent event) {
        return new OutboxMessage(
                "SETTLEMENT",
                UUID.randomUUID(),
                event.getClass().getSimpleName(),
                event.idempotencyKey(),
                sellerApprovedTopic,
                jsonUtil.jsonToString(event),
                3,
                OutboxStatus.PENDING
        );
    }

    public OutboxMessage paymentRefundFailed(PaymentRefundFailedEvent event) {
        return new OutboxMessage(
                "REFUND_FAIL",
                event.orderId(),
                event.getClass().getSimpleName(),
                UUID.randomUUID(),
                orderFailedTopic,
                jsonUtil.jsonToString(event),
                3,
                OutboxStatus.PENDING
        );
    }

    public OutboxMessage paymentRefunded(PaymentRefundSucceededEvent event) {
        return new OutboxMessage(
                "REFUND_SUCCESS",
                event.orderId(),
                event.getClass().getSimpleName(),
                UUID.randomUUID(),
                orderRefundTopic,
                jsonUtil.jsonToString(event),
                3,
                OutboxStatus.PENDING
        );
    }
}
