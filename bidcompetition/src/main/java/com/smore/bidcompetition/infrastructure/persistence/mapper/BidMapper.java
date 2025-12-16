package com.smore.bidcompetition.infrastructure.persistence.mapper;

import com.smore.bidcompetition.domain.model.BidCompetition;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;

public final class BidMapper {

    private BidMapper() {

    }

    public static BidCompetitionEntity toEntityForCreate(BidCompetition bidCompetition) {
        if (bidCompetition == null) {
            return null;
        }

        return BidCompetitionEntity.create(
            bidCompetition.getProductId(),
            bidCompetition.getCategoryId(),
            bidCompetition.getSellerId(),
            bidCompetition.getProductPrice(),
            bidCompetition.getTotalQuantity(),
            bidCompetition.getStock(),
            bidCompetition.getBidStatus(),
            bidCompetition.getIdempotencyKey(),
            bidCompetition.getStartAt(),
            bidCompetition.getEndAt()
        );
    }

    public static BidCompetition toDomain(BidCompetitionEntity entity) {
        if (entity == null) {
            return null;
        }

        return BidCompetition.of(
            entity.getId(),
            entity.getProductId(),
            entity.getCategoryId(),
            entity.getSellerId(),
            entity.getProductPrice(),
            entity.getTotalQuantity(),
            entity.getStock(),
            entity.getBidStatus(),
            entity.getIdempotencyKey(),
            entity.getStartAt(),
            entity.getEndAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt(),
            entity.getDeletedBy()
        );
    }
}
