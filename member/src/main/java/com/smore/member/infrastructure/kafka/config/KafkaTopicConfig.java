package com.smore.member.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaTopicConfig {

    private static final String SAMPLE_TOPIC = "member-sample";

    /**
     * 오프셋 토픽 자동 생성이 꺼져 있으므로 리스너용 샘플 토픽을 생성한다.
     */
    @Bean
    public NewTopic memberSampleTopic() {
        return TopicBuilder.name(SAMPLE_TOPIC)
            .partitions(1)
            .replicas(1)
            .build();
    }
}
