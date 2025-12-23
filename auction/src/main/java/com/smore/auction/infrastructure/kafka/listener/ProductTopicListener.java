package com.smore.auction.infrastructure.kafka.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.usecase.AuctionCreate;
import com.smore.auction.application.usecase.AuctionStart;
import com.smore.auction.application.usecase.ReleaseWinner;
import com.smore.auction.infrastructure.inbox.InboxHandler;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionPendingStartedV1;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionStartedV1;
import com.smore.auction.infrastructure.kafka.listener.dto.OrderFailedV1;
import com.smore.auction.infrastructure.kafka.mapper.AuctionKafkaMapper;
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

    private final InboxHandler inboxHandler;
    private final ObjectMapper objectMapper;
    private final AuctionCreate auctionCreate;
    private final AuctionStart auctionStart;
    private final AuctionKafkaMapper appMapper;
    private final ReleaseWinner releaseWinner;

    // TODO: 실패시 처리로직 등 추가구현 필요
    @KafkaListener(
        topics = "${topic.auction-pending-start.v1}"
    )
    public void productAuctionPendingStartedV1(String event, Acknowledgment ack)
        throws JsonProcessingException {
        var auctionPendingStartedV1 =
            objectMapper.readValue(event, AuctionPendingStartedV1.class);
        inboxHandler.processOnce(
            auctionPendingStartedV1.idempotencyKey(),
            () -> auctionCreate.create(appMapper.toCommand(auctionPendingStartedV1))
        );
        ack.acknowledge();
    }

    @KafkaListener(
        topics = "${topic.auction-started.v1}"
    )
    public void productAuctionStarted(String event, Acknowledgment ack)
        throws JsonProcessingException {
        var auctionStartedV1 =
            objectMapper.readValue(event, AuctionStartedV1.class);

        inboxHandler.processOnce(
            auctionStartedV1.idempotencyKey(),
            () -> auctionStart.start(appMapper.toCommand(auctionStartedV1))
        );
        ack.acknowledge();
    }

    @KafkaListener(
        topics = "${topic.auction-failed.v1}"
    )
    public void order(String event, Acknowledgment ack)
        throws JsonProcessingException {
        var orderFailedV1 =
            objectMapper.readValue(event, OrderFailedV1.class);

        inboxHandler.processOnce(
            orderFailedV1.idempotencyKey(),
            () -> releaseWinner.releaseFromProductId(orderFailedV1.productId(),
                orderFailedV1.userId())
        );
        ack.acknowledge();
    }
}
