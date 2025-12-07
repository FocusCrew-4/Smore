package com.smore.auction.application.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.command.AuctionCreateCommand;
import com.smore.auction.application.service.usecase.AuctionCreate;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.model.Auction;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
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
    private final ObjectMapper objectMapper;

    // TODO: 예외타입 변환 필요
    @Override
    @SneakyThrows
    public Auction create(AuctionCreateCommand command) {
        if (auctionSqlRepository.findByProductId(command.productId()) != null) {
            throw new IllegalArgumentException("Auction already exists");
        }

        log.info("Creating auction {}", objectMapper.writeValueAsString(command));

        var auction = Auction.create(
            command.productId(),
            command.productPrice(),
            command.stock(),
            command.sellerId(),
            clock
        );

        log.info("Created auction {}", objectMapper.writeValueAsString(auction));

        Auction savedAuction = auctionSqlRepository.save(auction);

        log.info("Saved auction {}", objectMapper.writeValueAsString(savedAuction));

        return savedAuction;
    }
}
