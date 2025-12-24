package com.smore.bidcompetition.application.repository;

import com.smore.bidcompetition.domain.model.Winner;
import com.smore.bidcompetition.domain.status.WinnerStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WinnerRepository {

    Winner findById(UUID winnerId);

    Winner findByIdempotencyKey(UUID bidId, UUID idempotencyKey);

    Winner findByAllocationKey(UUID allocationKey);

    Page<UUID> findExpiredWinners(LocalDateTime now, long bufferTime, Pageable pageable);

    int winnerPaid(UUID allocationKey, UUID orderId, Long version);

    Winner save(Winner winner);

    int markCancelled(UUID bidId, UUID allocationKey, Collection<WinnerStatus> statuses, Long version);

    int markExpired(UUID winnerId, Long version);
}
