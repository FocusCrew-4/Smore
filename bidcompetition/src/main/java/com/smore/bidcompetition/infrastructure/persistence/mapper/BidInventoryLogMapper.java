package com.smore.bidcompetition.infrastructure.persistence.mapper;

import com.smore.bidcompetition.domain.model.BidInventoryLog;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidInventoryLogEntity;

public final class BidInventoryLogMapper {

    private BidInventoryLogMapper() {

    }

    public static BidInventoryLogEntity toEntityForCreate(BidInventoryLog log) {
        if (log == null) {
            return null;
        }

        return BidInventoryLogEntity.create(
            log.getBidId(),
            log.getWinnerId(),
            log.getChangeType(),
            log.getStockBefore(),
            log.getStockAfter(),
            log.getQuantityDelta(),
            log.getIdempotencyKey(),
            log.getCreatedAt()
        );
    }

    public static BidInventoryLog toDomain(BidInventoryLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return BidInventoryLog.of(
            entity.getId(),
            entity.getBidId(),
            entity.getWinnerId(),
            entity.getChangeType(),
            entity.getStockBefore(),
            entity.getStockAfter(),
            entity.getQuantityDelta(),
            entity.getIdempotencyKey(),
            entity.getCreatedAt()
        );
    }
}
