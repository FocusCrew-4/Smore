package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.model.BidCompetition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public interface BidCompetitionRepository {

    BidCompetition findByIdempotencyKey(UUID idempotencyKey);

    BidCompetition findByIdForUpdate(UUID bidId);

    BidCompetition save(BidCompetition bidCompetition);

    int decreaseStock(UUID bidId, Integer quantity, LocalDateTime now);
}
