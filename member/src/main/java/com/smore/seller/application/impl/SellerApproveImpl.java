package com.smore.seller.application.impl;

import com.smore.seller.application.usecase.SellerApprove;
import com.smore.seller.domain.events.SellerRegisterV1Event;
import com.smore.seller.domain.model.Seller;
import com.smore.seller.domain.repository.SellerRepository;
import com.smore.seller.infrastructure.kafka.SellerEventSerializer;
import com.smore.seller.infrastructure.persistence.jpa.entity.SellerOutbox;
import com.smore.seller.infrastructure.persistence.jpa.repository.SellerOutboxRepository;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerApproveImpl implements SellerApprove {

    private final SellerRepository repository;
    private final SellerOutboxRepository outboxRepository;
    private final Clock clock;
    private final SellerEventSerializer serializer;

    @Override
    public void approveSeller(UUID uuid) {
        Seller seller = repository.findById(uuid);
        approve(seller);
    }

    @Override
    public void approveSeller(Long id) {
        Seller seller = repository.findByMemberId(id);
        approve(seller);
    }

    private void approve(Seller seller) {
        seller.approveApply(clock);
        var event
            = SellerRegisterV1Event.create(seller.getMemberId(), UUID.randomUUID(), clock);
        SellerOutbox outbox = new SellerOutbox(
            "seller.register.v1",
            seller.getMemberId(),
            serializer.toJsonString(event),
            clock
        );
        outboxRepository.save(outbox);
        repository.save(seller);
    }
}
