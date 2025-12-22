package com.smore.auction.infrastructure.kafka;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
@RequiredArgsConstructor
public class AuctionKafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private final AuctionKafkaTopicProperties topicProperties;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
        );

        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic AuctionWinnerConfirmV1() {
        return TopicBuilder.name(topicProperties.getWinnerConfirm().get("v1"))
            .partitions(2)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic testMark() {
        return TopicBuilder.name("TEST_MARK")
            .partitions(2)
            .replicas(3)
            .build();
    }

}
