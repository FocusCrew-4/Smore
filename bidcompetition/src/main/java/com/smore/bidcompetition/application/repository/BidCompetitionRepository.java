package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.model.BidCompetition;
import java.util.UUID;

public interface BidCompetitionRepository {

    BidCompetition findByIdempotencyKey(UUID idempotencyKey);

    BidCompetition save(BidCompetition bidCompetition);
}
