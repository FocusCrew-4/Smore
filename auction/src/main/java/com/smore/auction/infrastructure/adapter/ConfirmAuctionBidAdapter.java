package com.smore.auction.infrastructure.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.command.BidConfirmCommand;
import com.smore.auction.application.service.usecase.ConfirmAuctionBid;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.events.AuctionWinnerConfirmV1;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.domain.model.AuctionBidderRank;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


// TODO: Auction 을 통해서 변경되도록 수정
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ConfirmAuctionBidAdapter implements ConfirmAuctionBid {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AuctionSqlRepository repository;
    private final ObjectMapper objectMapper;

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
                UUID.randomUUID()
            );

        var future = kafkaTemplate.send(
            "auction.winnerConfirm.v1",
            command.requesterId().toString(),
            objectMapper.writeValueAsString(event)
        );

        repository.saveBidder(auctionBidderRank);

        future.get();
    }
}
