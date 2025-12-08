package com.smore.order.application.command;

import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.OutboxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j(topic = "SuccessRefundCommand")
public class RefundSuccessCommand implements OutboxCommand{

    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Outbox outbox;

    public RefundSuccessCommand(String topic, KafkaTemplate<String, String> kafkaTemplate, Outbox outbox) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
        this.outbox = outbox;
    }

    @Override
    public OutboxResult execute() {
        log.info("SuccessRefundCommand 이벤트 발행 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
            outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

        try {
            kafkaTemplate.send(topic, outbox.getPayload())
                .get();

            log.info("SuccessRefundCommand 이벤트 발행 성공 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
                outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

            return OutboxResult.SUCCESS;
        } catch (Exception e) {

            log.error("SuccessRefundCommand 이벤트 발행 실패 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
                outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

            return OutboxResult.FAIL;
        }
    }
}
