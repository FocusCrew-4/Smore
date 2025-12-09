package com.smore.auction.infrastructure.kafka.mapper;

import com.smore.auction.application.command.AuctionCreateCommand;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionStartedV1;

public interface AuctionKafkaMapper {
    AuctionCreateCommand toCommand(AuctionStartedV1 auctionStartedV1);
}
