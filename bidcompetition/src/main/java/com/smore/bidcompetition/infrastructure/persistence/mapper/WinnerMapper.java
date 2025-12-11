package com.smore.bidcompetition.infrastructure.persistence.mapper;

import com.smore.bidcompetition.domain.model.Winner;
import com.smore.bidcompetition.infrastructure.persistence.entity.WinnerEntity;

public final class WinnerMapper {

    private WinnerMapper() {

    }

    public static WinnerEntity toEntityForCreate(Winner winner) {
        if (winner == null) {
            return null;
        }

        return WinnerEntity.create(
            winner.getUserId(),
            winner.getBidId(),
            winner.getProductId(),
            winner.getQuantity(),
            winner.getAllocationKey(),
            winner.getIdempotencyKey(),
            winner.getWinnerStatus(),
            winner.getOrderedAt(),
            winner.getExpireAt()
        );
    }

    public static Winner toDomain(WinnerEntity entity) {
        if (entity == null) {
            return null;
        }

        return Winner.of(
            entity.getId(),
            entity.getUserId(),
            entity.getBidId(),
            entity.getOrderId(),
            entity.getProductId(),
            entity.getQuantity(),
            entity.getAllocationKey(),
            entity.getIdempotencyKey(),
            entity.getWinnerStatus(),
            entity.getOrderedAt(),
            entity.getExpireAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt(),
            entity.getDeletedBy(),
            entity.getVersion()
        );
    }
}
