package com.smore.auction.application.usecase.impl;

import com.smore.auction.application.command.AuctionCreateCommand;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.application.usecase.AuctionCreate;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.infrastructure.exception.InfraErrorCode;
import com.smore.auction.infrastructure.exception.InfraException;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuctionCreateImpl implements AuctionCreate {

    private final AuctionSqlRepository auctionSqlRepository;
    private final Clock clock;

    @Override
    public Auction create(AuctionCreateCommand command) {
        if (auctionSqlRepository.findByProductId(command.productId()) != null) {
            throw new InfraException(InfraErrorCode.AUCTION_ALREADY_EXIST);
        }
        var auction = Auction.create(
            command.productId(),
            command.productCategoryId(),
            command.productPrice(),
            command.stock(),
            command.sellerId(),
            clock
        );
        var savedAuction
            = auctionSqlRepository.save(auction);
        return savedAuction;
    }
}
