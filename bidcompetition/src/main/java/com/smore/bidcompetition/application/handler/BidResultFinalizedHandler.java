package com.smore.bidcompetition.application.handler;

import com.smore.bidcompetition.domain.model.Outbox;
import com.smore.bidcompetition.domain.status.OutboxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j(topic = "BidResultFinalizedHandler")
public class BidResultFinalizedHandler implements OutboxHandler {

    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Outbox outbox;

    public BidResultFinalizedHandler(String topic, KafkaTemplate<String, String> kafkaTemplate, Outbox outbox) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
        this.outbox = outbox;
    }

    @Override
    public OutboxResult execute() {
        log.info("BidResultFinalized 이벤트 발행 - 도메인 : {}, 이벤트 : {}, productId : {}, ",
            outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

        try {
            kafkaTemplate.send(topic, outbox.getPayload())
                .get();

            log.info("BidResultFinalized 이벤트 발행 성공 - 도메인 : {}, 이벤트 : {}, productId : {}, ",
                outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

            return OutboxResult.SUCCESS;
        } catch (Exception e) {

            log.error("BidResultFinalized 이벤트 발행 실패 - 도메인 : {}, 이벤트 : {}, productId : {}, ",
                outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

            return OutboxResult.FAIL;
        }
    }

}
