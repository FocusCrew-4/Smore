package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BidCompetitionJpaRepositoryCustom {

    BidCompetitionEntity findByIdempotencyKey(UUID idempotencyKey);

    BidCompetitionEntity findByIdForUpdate(UUID bidId);

    List<UUID> findBidsToActivate(LocalDateTime now);

    List<UUID> findBidsToClose(LocalDateTime now);

    List<UUID> findBidsToEnd(LocalDateTime now, long closeGraceSeconds);

    int decreaseStock(UUID bidId, Integer quantity, LocalDateTime acceptedAt);

    int increaseStock(UUID bidId, Integer quantity);

    int bulkActivateByStartAt(List<UUID> ids, LocalDateTime now);

    int bulkCloseByEndAt(List<UUID> ids, LocalDateTime now);

    int bulkFinalizeByValidAt(List<UUID> ids, LocalDateTime now, long closeGraceSeconds);
}
