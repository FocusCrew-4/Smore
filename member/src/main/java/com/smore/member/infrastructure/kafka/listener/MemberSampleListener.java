package com.smore.member.infrastructure.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class MemberSampleListener {

    @KafkaListener(
        topics = "member-sample"
    )
    public void consume(String message) {
        log.info("Received message on member-sample: {}", message);
    }
}
