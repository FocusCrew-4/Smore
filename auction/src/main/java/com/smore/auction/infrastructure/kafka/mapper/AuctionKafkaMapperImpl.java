package com.smore.auction.infrastructure.kafka.mapper;

import com.smore.auction.application.command.AuctionCreateCommand;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionStartedV1;
import org.springframework.stereotype.Component;

@Component
public class AuctionKafkaMapperImpl implements AuctionKafkaMapper {
    @Override
    public AuctionCreateCommand toCommand(AuctionStartedV1 auctionStartedV1) {
        return new  AuctionCreateCommand(
            auctionStartedV1.productId(),
            auctionStartedV1.productPrice(),
            auctionStartedV1.stock(),
            auctionStartedV1.sellerId()
        );
    }
}
