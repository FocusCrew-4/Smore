package com.smore.auction.infrastructure.adapter.out;

import com.smore.auction.application.port.out.AuctionBidLogger;
import com.smore.auction.infrastructure.mongo.AuctionBidLogDocument;
import com.smore.auction.infrastructure.mongo.AuctionBidLogRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionBidLoggerImpl implements AuctionBidLogger {

    private final AuctionBidLogRepository bidLogRepository;

    @Override
    public void writeBidLog(UUID auctionId, Long bidderId, Integer quantity, Double bidPrice) {
        AuctionBidLogDocument log =
            AuctionBidLogDocument.builder()
                .auctionId(auctionId)
                .bidderId(bidderId)
                .quantity(quantity)
                .bidPrice(bidPrice)
                .createdAt(Instant.now())
                .build();

        bidLogRepository.save(log);
    }
}
