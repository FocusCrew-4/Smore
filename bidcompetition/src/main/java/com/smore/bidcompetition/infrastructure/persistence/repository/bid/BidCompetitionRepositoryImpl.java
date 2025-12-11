package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import com.smore.bidcompetition.application.repository.BidCompetitionRepository;
import com.smore.bidcompetition.domain.model.BidCompetition;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import com.smore.bidcompetition.infrastructure.persistence.mapper.BidMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j(topic = "BidCompetitionRepositoryImpl")
@Repository
@RequiredArgsConstructor
public class BidCompetitionRepositoryImpl implements BidCompetitionRepository {

    private final BidCompetitionJpaRepository bidCompetitionJpaRepository;

    @Override
    public BidCompetition findByIdempotencyKey(UUID idempotencyKey) {

        BidCompetitionEntity entity = bidCompetitionJpaRepository.findByIdempotencyKey(idempotencyKey);

        if (entity == null) return null;

        return BidMapper.toDomain(entity);
    }

    @Override
    public BidCompetition save(BidCompetition bidCompetition) {

        BidCompetitionEntity entity = bidCompetitionJpaRepository.save(
            BidMapper.toEntityForCreate(bidCompetition));

        return BidMapper.toDomain(entity);
    }
}
