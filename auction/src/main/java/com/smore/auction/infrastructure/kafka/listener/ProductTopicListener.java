package com.smore.auction.infrastructure.kafka.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.infrastructure.kafka.mapper.AuctionKafkaMapper;
import com.smore.auction.application.service.usecase.AuctionCreate;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionStartedV1;
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
    private final AuctionCreate auctionCreate;
    private final AuctionKafkaMapper appMapper;

    // TODO: 실패시 처리로직 등 추가구현 필요
    @KafkaListener(
        topics = "${product.topic.auction-pending-start.v1}"
    )
    public void productAuctionStartedV1(String event, Acknowledgment ack) {
        try {

            log.info("Received event {}", event);

            var auctionStartedV1
                = objectMapper.readValue(event, AuctionStartedV1.class);

            log.info("AuctionStartedV1 received {}", auctionStartedV1);

            auctionCreate.create(appMapper.toCommand(auctionStartedV1));

            ack.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage(), e);

        }
    }

}
