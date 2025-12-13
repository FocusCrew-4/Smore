package com.smore.bidcompetition.infrastructure.persistence.repository.winner;

import com.smore.bidcompetition.application.repository.WinnerRepository;
import com.smore.bidcompetition.domain.model.Winner;
import com.smore.bidcompetition.domain.status.WinnerStatus;
import com.smore.bidcompetition.infrastructure.error.BidErrorCode;
import com.smore.bidcompetition.infrastructure.persistence.entity.WinnerEntity;
import com.smore.bidcompetition.infrastructure.persistence.exception.NotFoundWinnerException;
import com.smore.bidcompetition.infrastructure.persistence.mapper.WinnerMapper;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j(topic = "WinnerRepositoryImpl")
@Repository
@RequiredArgsConstructor
public class WinnerRepositoryImpl implements WinnerRepository {

    private final WinnerJpaRepository winnerJpaRepository;

    @Override
    public Winner findByIdempotencyKey(UUID idempotencyKey) {

        WinnerEntity entity = winnerJpaRepository.findByIdempotencyKey(idempotencyKey);

        if (entity == null) return null;

        return WinnerMapper.toDomain(entity);
    }

    @Override
    public Winner findByAllocationKey(UUID allocationKey) {

        WinnerEntity entity = winnerJpaRepository.findByAllocationKey(allocationKey);

        if (entity == null) throw new NotFoundWinnerException(BidErrorCode.NOT_FOUND_WINNER);

        return WinnerMapper.toDomain(entity);
    }

    @Override
    public Winner save(Winner winner) {

        WinnerEntity entity = winnerJpaRepository.save(
            WinnerMapper.toEntityForCreate(winner)
        );

        return WinnerMapper.toDomain(entity);
    }

    @Override
    public int winnerPaid(UUID allocationKey, UUID orderId, Long version) {
        return winnerJpaRepository.winnerPaid(allocationKey, orderId, version);
    }

    @Override
    public int markCancelled(UUID bidId, UUID allocationKey, Collection<WinnerStatus> statuses, Long version) {
        return winnerJpaRepository.markCancelled(bidId, allocationKey, statuses, version);
    }
}
