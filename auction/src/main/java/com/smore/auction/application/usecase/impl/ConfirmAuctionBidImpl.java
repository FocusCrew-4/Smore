package com.smore.auction.application.usecase.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.command.BidConfirmCommand;
import com.smore.auction.application.port.out.AuctionOutboxPort;
import com.smore.auction.application.usecase.ConfirmAuctionBid;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.events.AuctionWinnerConfirmV1;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.domain.model.AuctionBidderRank;
import com.smore.auction.infrastructure.kafka.AuctionKafkaTopicProperties;
import com.smore.auction.infrastructure.outbox.AuctionOutbox;
import jakarta.transaction.Transactional;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ConfirmAuctionBidImpl implements ConfirmAuctionBid {

    private final AuctionKafkaTopicProperties topic;
    private final AuctionOutboxPort auctionOutboxPort;
    private final AuctionSqlRepository repository;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @SneakyThrows
    @Override
    public void confirm(BidConfirmCommand command) {
        Auction auction
            = repository.findById(String.valueOf(command.auctionId()));

        AuctionBidderRank auctionBidderRank
            = repository.findBidByAuctionIdAndBidderId(String.valueOf(command.auctionId()), command.requesterId());

        auctionBidderRank.confirm();

        var event =
            AuctionWinnerConfirmV1.create(
                command.requesterId(),
                auction.getProduct().id(),
                auction.getProduct().price(),
                auctionBidderRank.getBidder().quantity(),
                auction.getProduct().categoryId(),
                auction.getSellerId(),
                UUID.randomUUID(),
                clock
            );

        try {
            String strEvent = objectMapper.writeValueAsString(event);
            var outboxData = new AuctionOutbox(
                topic.getWinnerConfirm().get("v1"),
                command.requesterId(),
                strEvent,
                clock
            );
            auctionOutboxPort.saveEvent(outboxData);
        } catch (JsonProcessingException e) {
            log.error("Json 파싱오류 ConfirmAuctionBidImpl 에서 발생: {}", e.getMessage());
        }

        repository.saveBidder(auctionBidderRank);
    }
}
