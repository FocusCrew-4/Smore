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

    @Value("${topic.order.refund}")
    private String orderRefundTopic;

    @Value("${topic.order.refund-fail}")
    private String orderRefundFailTopic;

    @Value("${topic.order.refund-dlt}")
    private String orderRefundDltTopic;

    @Value("${topic.seller.success}")
    private String sellerSuccessTopic;

    @Value("${topic.seller.failed}")
    private String sellerFailedTopic;

    @Value("${topic.seller.dlt}")
    private String sellerDltTopic;

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

    @Bean
    public NewTopic paymentRefundTopic() {
        return TopicBuilder.name(orderRefundTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic paymentRefundFailTopic() {
        return TopicBuilder.name(orderRefundFailTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic paymentRefundDltTopic() {
        return TopicBuilder.name(orderRefundDltTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic sellerSuccessTopic() {
        return TopicBuilder.name(sellerSuccessTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic sellerFailedTopic() {
        return TopicBuilder.name(sellerFailedTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }

    @Bean
    public NewTopic sellerDltTopic() {
        return TopicBuilder.name(sellerDltTopic)
                .partitions(3)
                .replicas(3)
                .build();
    }
}
