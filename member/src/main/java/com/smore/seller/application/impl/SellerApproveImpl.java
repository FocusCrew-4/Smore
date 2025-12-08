package com.smore.seller.application.impl;

import com.smore.seller.application.port.EventSerializerPort;
import com.smore.seller.application.port.SellerTopicPort;
import com.smore.seller.application.port.out.OutboxRepositoryPort;
import com.smore.seller.application.usecase.SellerApprove;
import com.smore.seller.domain.events.SellerRegisterV1Event;
import com.smore.seller.domain.model.Seller;
import com.smore.seller.domain.repository.SellerRepository;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SellerApproveImpl implements SellerApprove {

    private final SellerRepository repository;
    private final Clock clock;
    private final OutboxRepositoryPort outboxRepository;
    private final EventSerializerPort eventSerializer;
    private final SellerTopicPort sellerTopic;

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
        outboxRepository.saveOutBox(
            sellerTopic.getSellerRegisterTopic("v1"),
            seller.getMemberId(),
            eventSerializer.serializeEvent(event),
            clock
        );
        repository.save(seller);
    }
}
