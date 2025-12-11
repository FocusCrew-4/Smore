package com.smore.payment.payment.infrastructure.kafka;

public interface MessageBrokerPublisher {
    void publish(String eventType, String payload, String key);
}