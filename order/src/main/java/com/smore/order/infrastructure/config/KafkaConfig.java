package com.smore.order.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${topic.order.created}")
    private String createdOrderTopic;

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(createdOrderTopic)
            .partitions(3)
            .replicas(1)
            .build();
    }
}
