package com.smore.auction.infrastructure.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.service.usecase.AuctionStart;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionStartedV1;
import com.smore.auction.infrastructure.kafka.mapper.AuctionKafkaMapper;
import com.smore.auction.application.service.usecase.AuctionCreate;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionPendingStartedV1;
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
public class ProductTopicListener {

    private final ObjectMapper objectMapper;
    // TODO: 인터페이스 풀고 Listener 가 하는일 직접 정의
    private final AuctionCreate auctionCreate;
    private final AuctionStart auctionStart;
    private final AuctionKafkaMapper appMapper;

    // TODO: 실패시 처리로직 등 추가구현 필요
    @KafkaListener(
        topics = "${topic.auction-pending-start.v1}"
    )
    public void productAuctionPendingStartedV1(String event, Acknowledgment ack) {
        try {

            log.info("Received event {}", event);

            var auctionPendingStartedV1
                = objectMapper.readValue(event, AuctionPendingStartedV1.class);

            log.info("AuctionStartedV1 received {}", auctionPendingStartedV1);

            auctionCreate.create(appMapper.toCommand(auctionPendingStartedV1));

            ack.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }
    }

    @KafkaListener(
        topics = "${topic.auction-started.v1}"
    )
    public void productAuctionStarted(String event, Acknowledgment ack) {
        try {
            var auctionStartedV1
                = objectMapper.readValue(event, AuctionStartedV1.class);

            auctionStart.start(appMapper.toCommand(auctionStartedV1));

            ack.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
