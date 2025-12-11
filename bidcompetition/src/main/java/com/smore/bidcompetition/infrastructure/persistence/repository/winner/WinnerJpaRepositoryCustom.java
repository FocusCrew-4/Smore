package com.smore.bidcompetition.infrastructure.persistence.repository.winner;

import com.smore.bidcompetition.infrastructure.persistence.entity.WinnerEntity;
import java.util.UUID;

public interface WinnerJpaRepositoryCustom {

    WinnerEntity findByIdempotencyKey(UUID idempotencyKey);
}
