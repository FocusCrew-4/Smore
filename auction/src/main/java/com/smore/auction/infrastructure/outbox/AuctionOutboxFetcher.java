package com.smore.auction.infrastructure.outbox;

import com.smore.auction.infrastructure.outbox.AuctionOutbox.MessageStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionOutboxFetcher {

    private final AuctionOutboxRepository auctionOutboxRepository;

    public List<AuctionOutbox> fetchPending() {
        return auctionOutboxRepository.findTop100ByStatusOrderByIdAsc(
            MessageStatus.PENDING
        );
    }

}
