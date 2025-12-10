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

    @Value("${topic.order.completed}")
    private String completedOrderTopic;

    @Value("${topic.order.refund}")
    private String refundRequestOrderTopic;

    @Value("${topic.order.refund-success}")
    private String refundSucceededOrderTopic;

    @Value("${topic.order.refund-fail}")
    private String refundFailedOrderTopic;

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(createdOrderTopic)
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic orderCompletedTopic() {
        return TopicBuilder.name(completedOrderTopic)
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic orderRefundRequestTopic() {
        return TopicBuilder.name(refundRequestOrderTopic)
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic orderRefundSucceededTopic() {
        return TopicBuilder.name(refundSucceededOrderTopic)
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic orderRefundFailedTopic() {
        return TopicBuilder.name(refundFailedOrderTopic)
            .partitions(3)
            .replicas(3)
            .build();
    }
}
