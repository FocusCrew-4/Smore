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
    // TODO: repo 에서 new 를 통해서 새로운 객체를 받아오는 것은 SRP 에 어긋난다 추후 DTO 와 mapper 를 통해 분리필요
    @Override
    public void saveOutBox(String topic, Long key, String payload, Clock clock) {
        SellerOutbox sellerOutbox = new SellerOutbox(
            topic,
            key,
            payload,
            clock
        );
        repository.save(sellerOutbox);
    }
}
