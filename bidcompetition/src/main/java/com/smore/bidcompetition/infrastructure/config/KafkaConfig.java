package com.smore.bidcompetition.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${topic.bid.winner-confirm}")
    private String bidWinnerConfirm;

    @Value("${topic.product.created}")
    private String productCreated;

    @Value("${topic.bid.finished}")
    private String bidFinished;

    @Bean
    public NewTopic bidWinnerConfirmTopic() {
        return TopicBuilder.name(bidWinnerConfirm)
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic productLimitedSalePendingStartTopic() {
        return TopicBuilder.name(productCreated)
            .partitions(3)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic limitedSalePendingStartTopic() {
        return TopicBuilder.name(bidFinished)
            .partitions(3)
            .replicas(3)
            .build();
    }

}
