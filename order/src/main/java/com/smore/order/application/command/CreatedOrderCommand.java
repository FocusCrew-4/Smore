package com.smore.order.application.command;

import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.OutboxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j(topic = "CreatedOrderCommand")
public class CreatedOrderCommand implements OutboxCommand {

    @Value("${topic.order.created}")
    private String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Outbox outbox;

    public CreatedOrderCommand(KafkaTemplate<String, String> kafkaTemplate, Outbox outbox) {
        this.kafkaTemplate = kafkaTemplate;
        this.outbox = outbox;
    }

    @Override
    public OutboxResult execute() {
        log.info("OrderService 이벤트 발행 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
            outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

        try {
            kafkaTemplate.send(topic, outbox.getPayload())
                .get();

            log.info("OrderService 이벤트 발행 성공 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
                outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

            return OutboxResult.SUCCESS;
        } catch (Exception e) {

            log.error("OrderService 이벤트 발행 실패 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
                outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

            return OutboxResult.FAIL;
        }
    }
}
