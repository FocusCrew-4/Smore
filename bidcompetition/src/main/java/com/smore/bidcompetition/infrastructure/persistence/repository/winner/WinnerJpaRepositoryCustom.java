package com.smore.bidcompetition.infrastructure.persistence.repository.winner;

import com.smore.bidcompetition.domain.status.WinnerStatus;
import com.smore.bidcompetition.infrastructure.persistence.entity.WinnerEntity;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WinnerJpaRepositoryCustom {

    WinnerEntity findByIdempotencyKey(UUID bidId, UUID idempotencyKey);

    WinnerEntity findByAllocationKey(UUID allocationKey);

    Page<UUID> findExpiredWinners(LocalDateTime now, long bufferTime, Pageable pageable);

    int winnerPaid(UUID allocationKey, UUID orderId, Long version);

    int markCancelled(UUID bidId, UUID allocationKey, Collection<WinnerStatus> statuses, Long version);

    int markExpired(UUID winnerId, Long version);
}
