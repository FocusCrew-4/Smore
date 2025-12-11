package com.smore.payment.payment.infrastructure.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher implements MessageBrokerPublisher{

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void publish(String topicName, String payload, String key) {
        kafkaTemplate.send(topicName, key, payload);
    }
}
