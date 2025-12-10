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
            bidCompetition.getSellerId(),
            bidCompetition.getStock(),
            bidCompetition.getBidStatus(),
            bidCompetition.getStartedAt(),
            bidCompetition.getClosedAt()
        );
    }

    public static BidCompetition toDomain(BidCompetitionEntity entity) {
        if (entity == null) {
            return null;
        }

        return BidCompetition.of(
            entity.getId(),
            entity.getProductId(),
            entity.getSellerId(),
            entity.getStock(),
            entity.getBidStatus(),
            entity.getStartedAt(),
            entity.getClosedAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt(),
            entity.getDeletedBy()
        );
    }
}
