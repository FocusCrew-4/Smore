package com.smore.bidcompetition.infrastructure.persistence.repository.inventorylog;

import com.smore.bidcompetition.infrastructure.persistence.entity.BidInventoryLogEntity;

public interface BidInventoryLogJpaRepositoryCustom {

    BidInventoryLogEntity findByIdempotencyKey(String idempotencyKey);
}
