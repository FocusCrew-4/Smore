package com.smore.auction.infrastructure.kafka.mapper;

import com.smore.auction.application.command.AuctionCreateCommand;
import com.smore.auction.application.command.AuctionStartCommand;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionPendingStartedV1;
import com.smore.auction.infrastructure.kafka.listener.dto.AuctionStartedV1;
import org.springframework.stereotype.Component;

@Component
public class AuctionKafkaMapperImpl implements AuctionKafkaMapper {
    @Override
    public AuctionCreateCommand toCommand(AuctionPendingStartedV1 auctionPendingStartedV1) {
        return new  AuctionCreateCommand(
            auctionPendingStartedV1.productId(),
            auctionPendingStartedV1.productCategoryId(),
            auctionPendingStartedV1.productPrice(),
            auctionPendingStartedV1.stock(),
            auctionPendingStartedV1.sellerId()
        );
    }

    @Override
    public AuctionStartCommand toCommand(AuctionStartedV1 auctionStartedV1) {
        return new AuctionStartCommand(
            auctionStartedV1.productId(),
            auctionStartedV1.expireAfter(),
            auctionStartedV1.idempotencyKey()
        );
    }
}
