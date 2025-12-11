package com.smore.payment.payment.infrastructure.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${topic.order.approved}")
    private String orderApprovedTopic;

    @Value("${topic.seller.approved}")
    private String sellerApprovedTopic;

    @Value("${topic.order.failed}")
    private String orderFailedTopic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic paymentApprovedTopic() {
        return TopicBuilder.name(orderApprovedTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic paymentSettlementTopic() {
        return TopicBuilder.name(sellerApprovedTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic paymentFailedTopic() {
        return TopicBuilder.name(orderFailedTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }
}
