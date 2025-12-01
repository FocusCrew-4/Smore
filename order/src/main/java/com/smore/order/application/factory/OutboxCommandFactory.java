package com.smore.order.application.factory;

import com.smore.order.application.command.CreatedOrderCommand;
import com.smore.order.application.command.OutboxCommand;
import com.smore.order.domain.model.Outbox;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxCommandFactory {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxCommand from(Outbox outbox) {
        return switch (outbox.getEventType()) {
            case ORDER_CREATED -> new CreatedOrderCommand(kafkaTemplate, outbox);
            case ORDER_COMPLETED -> null;
            case ORDER_FAILED -> null;
            case PAYMENT_CANCEL -> null;
            case ORDER_CANCELLED -> null;
            default -> throw new IllegalArgumentException(
                "지원되지 않은 이벤트입니다." + outbox.getEventType()
            );
        };
    }
}
