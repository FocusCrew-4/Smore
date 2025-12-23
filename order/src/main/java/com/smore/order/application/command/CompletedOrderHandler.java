package com.smore.order.application.command;

import com.smore.order.domain.model.Outbox;
import com.smore.order.domain.status.OutboxResult;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j(topic = "CompletedOrderCommand")
public class CompletedOrderHandler implements OutboxHandler {

    private final String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Outbox outbox;
    private final Tracer tracer;
    private final Propagator propagator;


    public CompletedOrderHandler(Tracer tracer, Propagator propagator, String topic, KafkaTemplate<String, String> kafkaTemplate, Outbox outbox) {
        this.propagator = propagator;
        this.tracer = tracer;
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
        this.outbox = outbox;
    }

    @Override
    public OutboxResult execute() {
        log.info("CompletedOrder 이벤트 발행 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
            outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

        Span newSpan = restoreAndStartSpan();

        try (Tracer.SpanInScope ws = tracer.withSpan(newSpan)) {
            try {
                kafkaTemplate.send(topic, outbox.getPayload())
                    .get();

                log.info("CompletedOrder 이벤트 발행 성공 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
                    outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId());

                return OutboxResult.SUCCESS;
            } catch (Exception e) {
                newSpan.error(e);

                log.error("CompletedOrder 이벤트 발행 실패 - 도메인 : {}, 이벤트 : {}, orderId : {}, ",
                    outbox.getAggregateType(), outbox.getEventType(), outbox.getAggregateId(), e);

                return OutboxResult.FAIL;
            }
        } finally {
            newSpan.end();
        }
    }

    private Span restoreAndStartSpan() {
        Span.Builder spanBuilder = propagator.extract(outbox, (carrier, key) -> {
            if ("X-B3-TraceId".equalsIgnoreCase(key)) {
                return carrier.getTraceId();
            }
            if ("X-B3-SpanId".equalsIgnoreCase(key)) {
                return carrier.getSpanId();
            }
            return null;
        });

        Span newSpan = spanBuilder
            .name("kafka-send")
            .start();
        return newSpan;
    }
}
