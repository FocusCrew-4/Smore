package com.smore.auction.infrastructure.adapter;

import com.smore.auction.application.port.out.AuctionOutboxPort;
import com.smore.auction.infrastructure.outbox.AuctionOutbox;
import com.smore.auction.infrastructure.outbox.AuctionOutboxRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class AuctionOutBoxPortImpl implements AuctionOutboxPort {

    private final AuctionOutboxRepository repository;

    @Override
    public void saveEvent(AuctionOutbox event) {
        repository.save(event);
    }
}
