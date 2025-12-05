package com.smore.seller.infrastructure.adaptor.out;

import com.smore.seller.application.port.out.OutboxRepositoryPort;
import com.smore.seller.infrastructure.outbox.SellerOutbox;
import com.smore.seller.infrastructure.outbox.SellerOutboxRepository;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryPortImpl implements OutboxRepositoryPort {

    private final SellerOutboxRepository repository;
    private final Clock clock;

    @Override
    public void saveOutBox(String topic, Long key, String payload) {
        SellerOutbox sellerOutbox = new SellerOutbox(
            topic,
            key,
            payload,
            clock
        );
        repository.save(sellerOutbox);
    }
}
