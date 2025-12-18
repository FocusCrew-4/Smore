package com.smore.order.application.factory;

import static com.smore.order.domain.status.EventType.*;

import com.smore.order.application.command.CompletedOrderHandler;
import com.smore.order.application.command.CreatedOrderHandler;
import com.smore.order.application.command.FailedOrderHandler;
import com.smore.order.application.command.OutboxHandler;
import com.smore.order.application.command.RefundFailedHandler;
import com.smore.order.application.command.RefundRequestHandler;
import com.smore.order.application.command.RefundSuccessHandler;
import com.smore.order.domain.model.Outbox;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxHandlerFactory {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Tracer tracer;
    private final Propagator propagator;

    // FIXME : Map 형식으로 변환할 것
    @Value("${topic.order.created}")
    private String orderCreatedTopic;

    @Value("${topic.order.completed}")
    private String orderCompletedTopic;

    @Value("${topic.order.refund}")
    private String refundRequestTopic;

    @Value("${topic.order.refund-success}")
    private String refundSuccessTopic;

    @Value("${topic.order.refund-fail}")
    private String refundFailTopic;

    @Value("${topic.order.failed}")
    private String orderFailedTopic;

    public OutboxHandler from(Outbox outbox) {
        return switch (outbox.getEventType()) {
            case ORDER_CREATED -> new CreatedOrderHandler(tracer, propagator, orderCreatedTopic, kafkaTemplate, outbox);
            case ORDER_COMPLETED -> new CompletedOrderHandler(tracer, propagator, orderCompletedTopic, kafkaTemplate, outbox);
            case REFUND_REQUEST -> new RefundRequestHandler(tracer, propagator, refundRequestTopic, kafkaTemplate, outbox);
            case REFUND_SUCCESS -> new RefundSuccessHandler(tracer, propagator, refundSuccessTopic, kafkaTemplate, outbox);
            case REFUND_FAIL -> new RefundFailedHandler(tracer, propagator, refundFailTopic, kafkaTemplate, outbox);
            case ORDER_FAILED -> new FailedOrderHandler(tracer, propagator, orderFailedTopic, kafkaTemplate, outbox);
            default -> throw new IllegalArgumentException(
                "지원되지 않은 이벤트입니다." + outbox.getEventType()
            );
        };
    }
}
