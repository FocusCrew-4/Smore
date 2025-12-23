package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.model.BidCompetition;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BidCompetitionRepository {

    BidCompetition findById(UUID bidId);

    BidCompetition findByIdempotencyKey(UUID idempotencyKey);

    BidCompetition findByIdForUpdate(UUID bidId);

    List<UUID> findBidsToActivate(LocalDateTime now);

    List<UUID> findBidsToClose(LocalDateTime now);

    List<UUID> findBidsToEnd(LocalDateTime now, long closeGraceSeconds);

    List<BidCompetition> findBidListToEnd(LocalDateTime now, long closeGraceSeconds);

    BidCompetition save(BidCompetition bidCompetition);

    int decreaseStock(UUID bidId, Integer quantity, LocalDateTime acceptedAt);

    int increaseStock(UUID bidId, Integer quantity);

    int bulkActivateByStartAt(List<UUID> ids, LocalDateTime now);

    int bulkCloseByEndAt(List<UUID> ids, LocalDateTime now);

    int bulkFinalizeByValidAt(List<UUID> ids, LocalDateTime now, long closeGraceSeconds);

    int finalizeByValidAt(UUID bidId, LocalDateTime now, long closeGraceSeconds);
}
