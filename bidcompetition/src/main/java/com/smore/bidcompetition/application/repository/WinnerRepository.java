package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.model.Winner;
import com.smore.bidcompetition.domain.status.WinnerStatus;
import java.util.Collection;
import java.util.UUID;

public interface WinnerRepository {

    Winner findByIdempotencyKey(UUID idempotencyKey);

    Winner findByAllocationKey(UUID allocationKey);

    int winnerPaid(UUID allocationKey, UUID orderId, Long version);

    Winner save(Winner winner);

    int markCancelled(UUID bidId, UUID allocationKey, Collection<WinnerStatus> statuses, Long version);
}
