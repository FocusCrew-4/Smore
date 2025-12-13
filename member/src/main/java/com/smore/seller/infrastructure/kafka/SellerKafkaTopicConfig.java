package com.smore.seller.infrastructure.kafka;

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
public class SellerKafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    private final SellerTopicProperties topicProperties;

    @Bean
    /*
     KafkaAdmin 이란 Spring-Kafka 라이브러리에서 제공하는 Kafka 관리용 Bean 객체
     토픽 생성, 삭제, 조회 같은 Kafka 클러스터의 관리 작업을 스프링 컨텍스트 내에서 수행하게 해줌
     spring.kafka.bootstrap-servers 를 설정해주면 자동으로 생성해주기도 하지만
     직업제어 필요시 정의가능
     */
    public KafkaAdmin kafkaAdmin() {
        // KafkaAdmin 이 접속할 Kafka 브로커 서버 정보를 담은 설정 맵
        Map<String, Object> configs = Map.of(
            AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers
        );

        // KafkaAdmin 빈을 직접 정의해서 쓰겠다는 의도
        return new KafkaAdmin(configs);
    }

    // 판매자 상태변경과 등록은 자주 일어나는 이벤트는 아니기 때문에 파티션 2
    @Bean
    public NewTopic SellerRegisterV1Topic() {
        return TopicBuilder.name(topicProperties.getSellerRegister().get("v1"))
            .partitions(2)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic SellerDisabledV1Topic() {
        return TopicBuilder.name("seller.disabled.v1")
            .partitions(2)
            .replicas(3)
            .build();
    }

    @Bean
    public NewTopic SellerSettlementV1() {
        return TopicBuilder.name("seller.settlement.v1")
            .partitions(2)
            .replicas(3)
            .build();
    }
}
