package com.smore.bidcompetition.application.service;

import com.smore.bidcompetition.application.dto.BidCreateCommand;
import com.smore.bidcompetition.application.repository.BidCompetitionRepository;
import com.smore.bidcompetition.domain.model.BidCompetition;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "BidCompetitionService")
@Service
@RequiredArgsConstructor
public class BidCompetitionService {

    private final BidCompetitionRepository bidCompetitionRepository;
    private final Clock clock;

    @Transactional
    public void createBid(BidCreateCommand command) {

        BidCompetition bid = bidCompetitionRepository.findByIdempotencyKey(
            command.getIdempotencyKey());

        if (bid != null) {
            log.info("이미 처리된 작업입니다.");
            return;
        }

        BidCompetition newBid = BidCompetition.create(
            command.getProductId(),
            command.getCategoryId(),
            command.getSellerId(),
            command.getProductPrice(),
            command.getStock(),
            command.getIdempotencyKey(),
            command.getStartAt(),
            command.getEndAt()
        );

        bidCompetitionRepository.save(newBid);
    }
}
