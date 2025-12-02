package com.smore.order.application.command;

import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.OutboxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j(topic = "CreatedOrderCommand")
public class CompletedOrderCommand implements OutboxCommand{

    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Outbox outbox;

    public CompletedOrderCommand(String topic, KafkaTemplate<String, String> kafkaTemplate, Outbox outbox) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
        this.outbox = outbox;
    }

    @Override
    public OutboxResult execute() {
        log.info("CompletedOrder 이벤트 발행 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
            outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

        try {
            kafkaTemplate.send(topic, outbox.getPayload())
                .get();

            log.info("CompletedOrder 이벤트 발행 성공 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
                outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

            return OutboxResult.SUCCESS;
        } catch (Exception e) {

            log.error("CompletedOrder 이벤트 발행 실패 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
                outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

            return OutboxResult.FAIL;
        }
    }
}
