package com.smore.seller.application.impl;

import com.smore.seller.application.command.SettlementRequestCommand;
import com.smore.seller.application.port.EventSerializerPort;
import com.smore.seller.application.port.SellerTopicPort;
import com.smore.seller.application.port.out.OutboxRepositoryPort;
import com.smore.seller.application.usecase.RequestSettlement;
import com.smore.seller.domain.events.SellerSettlementV1Event;
import com.smore.seller.domain.model.Seller;
import com.smore.seller.domain.repository.SellerRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.NoContentException;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestSettlementImpl implements RequestSettlement {

    private final OutboxRepositoryPort outboxRepository;
    private final SellerRepository repository;
    private final EventSerializerPort serializer;
    private final SellerTopicPort topic;
    private final Clock clock;

    @Override
    public void sendSettlementRequest(SettlementRequestCommand command) throws NoContentException {
        Seller findSeller = repository.findByMemberId(command.requesterId());
        if (findSeller == null) {
            throw new NoContentException("Seller not found");
        }

        findSeller.debitBalanceForSettlement(command.amount(), clock);

        var event
            = SellerSettlementV1Event.create(
            command.requesterId(),
            command.amount(),
            findSeller.getAccountNum(),
            UUID.randomUUID(),
            clock
        );

        repository.save(findSeller);

        outboxRepository.saveOutBox(
            topic.getSellerSettlementTopic("v1"),
            command.requesterId(),
            serializer.serializeEvent(event),
            clock
        );
    }
}
