package com.smore.member.infrastructure.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.member.application.service.usecase.MemberRoleChange;
import com.smore.member.infrastructure.kafka.config.MemberTopicProperties;
import com.smore.member.infrastructure.kafka.listener.dto.SellerRegisterV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
@RequiredArgsConstructor
public class MemberSampleListener {

    private final MemberRoleChange memberRoleChange;
    private final ObjectMapper objectMapper;

    @KafkaListener(
        topics = "${member.topic.seller-register.v1}"
    )
    public void consume(String event, Acknowledgment ack) {
        try {
            log.info("Received message on member-sample: {}", event);

            // 처리 로직
            // ...
            SellerRegisterV1 sellerRegisterV1 = objectMapper.readValue(event, SellerRegisterV1.class);
            log.info("SellerRegisterV1: {}", sellerRegisterV1);
            log.info("SellerRegisterV1: {}", sellerRegisterV1.memberId());
            log.info("SellerRegisterV1: {}", sellerRegisterV1.idempotencyKey());
            memberRoleChange.toSeller(sellerRegisterV1.memberId());

            ack.acknowledge(); // 수동 offset commit 필수
        } catch (Exception e) {
            log.error("Error processing message", e);
            // ack 안 함 → 메시지 다시 받을 수 있음
            // 또는 retry, DLQ 처리 전략 선택
        }

    }
}
