package com.smore.auction.application.service.Impl;

import com.smore.auction.application.command.AuctionCreateCommand;
import com.smore.auction.application.service.usecase.AuctionCreate;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.model.Auction;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionCreateImpl implements AuctionCreate {

    private final AuctionSqlRepository auctionSqlRepository;
    private final Clock clock;

    // TODO: 예외타입 변환 필요
    @Override
    public Auction create(AuctionCreateCommand command) {
        if (auctionSqlRepository.findByProductId(command.productId()) != null) {
            throw new IllegalArgumentException("Auction already exists");
        }

        var auction = Auction.create(
            command.productId(),
            command.productPrice(),
            command.stock(),
            command.sellerId(),
            clock
        );

        Auction savedAuction = auctionSqlRepository.save(auction);

        return savedAuction;
    }
}
