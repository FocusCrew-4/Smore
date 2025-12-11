package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import com.smore.bidcompetition.application.repository.BidCompetitionRepository;
import com.smore.bidcompetition.domain.model.BidCompetition;
import com.smore.bidcompetition.infrastructure.error.BidErrorCode;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import com.smore.bidcompetition.infrastructure.persistence.exception.NotFoundBidException;
import com.smore.bidcompetition.infrastructure.persistence.mapper.BidMapper;
import java.time.LocalDateTime;
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
    public BidCompetition findById(UUID bidId) {

        BidCompetitionEntity entity = bidCompetitionJpaRepository.findById(bidId).orElseThrow(
            () -> new NotFoundBidException(BidErrorCode.NOT_FOUND_BID)
        );
        return BidMapper.toDomain(entity);
    }

    @Override
    public BidCompetition findByIdempotencyKey(UUID idempotencyKey) {

        BidCompetitionEntity entity = bidCompetitionJpaRepository.findByIdempotencyKey(idempotencyKey);

        if (entity == null) return null;

        return BidMapper.toDomain(entity);
    }

    @Override
    public BidCompetition findByIdForUpdate(UUID bidId) {

        BidCompetitionEntity entity = bidCompetitionJpaRepository.findByIdForUpdate(bidId);

        if (entity == null) {
            log.error("Bid를 찾을 수 없습니다. bidId : {}, method : {}", bidId, "findByIdForUpdate");
            throw new NotFoundBidException(BidErrorCode.NOT_FOUND_BID);
        }

        return BidMapper.toDomain(entity);
    }

    @Override
    public BidCompetition save(BidCompetition bidCompetition) {

        BidCompetitionEntity entity = bidCompetitionJpaRepository.save(
            BidMapper.toEntityForCreate(bidCompetition));

        return BidMapper.toDomain(entity);
    }

    @Override
    public int decreaseStock(UUID bidId, Integer quantity, LocalDateTime now) {
        return bidCompetitionJpaRepository.decreaseStock(bidId, quantity, now);
    }
}
