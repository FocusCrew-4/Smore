package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import java.time.LocalDateTime;
import java.util.UUID;

public interface BidCompetitionJpaRepositoryCustom {

    BidCompetitionEntity findByIdempotencyKey(UUID idempotencyKey);

    BidCompetitionEntity findByIdForUpdate(UUID bidId);

    int decreaseStock(UUID bidId, Integer quantity, LocalDateTime now);

    int increaseStock(UUID bidId, Integer quantity);

}
