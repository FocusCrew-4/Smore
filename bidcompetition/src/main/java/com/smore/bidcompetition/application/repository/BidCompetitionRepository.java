package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.model.BidCompetition;
import java.time.LocalDateTime;
import java.util.UUID;

public interface BidCompetitionRepository {

    BidCompetition findById(UUID bidId);

    BidCompetition findByIdempotencyKey(UUID idempotencyKey);

    BidCompetition findByIdForUpdate(UUID bidId);

    BidCompetition save(BidCompetition bidCompetition);

    int decreaseStock(UUID bidId, Integer quantity, LocalDateTime acceptedAt);

    int increaseStock(UUID bidId, Integer quantity);
}
