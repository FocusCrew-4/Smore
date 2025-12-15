package com.smore.bidcompetition.infrastructure.persistence.repository.winner;

import com.smore.bidcompetition.domain.status.WinnerStatus;
import com.smore.bidcompetition.infrastructure.persistence.entity.WinnerEntity;
import java.util.Collection;
import java.util.UUID;

public interface WinnerJpaRepositoryCustom {

    WinnerEntity findByIdempotencyKey(UUID idempotencyKey);

    WinnerEntity findByAllocationKey(UUID allocationKey);

    int winnerPaid(UUID allocationKey, UUID orderId, Long version);

    int markCancelled(UUID bidId, UUID allocationKey, Collection<WinnerStatus> statuses, Long version);
}
