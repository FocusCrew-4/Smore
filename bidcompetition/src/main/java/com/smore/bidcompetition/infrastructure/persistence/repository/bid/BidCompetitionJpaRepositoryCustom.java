package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import java.util.UUID;

public interface BidCompetitionJpaRepositoryCustom {

    BidCompetitionEntity findByIdempotencyKey(UUID idempotencyKey);

}
