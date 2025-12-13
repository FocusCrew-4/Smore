package com.smore.bidcompetition.infrastructure.persistence.repository.inventorylog;

import com.smore.bidcompetition.application.repository.BidInventoryLogRepository;
import com.smore.bidcompetition.domain.model.BidInventoryLog;
import com.smore.bidcompetition.infrastructure.persistence.entity.BidInventoryLogEntity;
import com.smore.bidcompetition.infrastructure.persistence.mapper.BidInventoryLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BidInventoryLogRepositoryImpl implements BidInventoryLogRepository {

    private final BidInventoryLogJpaRepository bidInventoryLogJpaRepository;

    @Override
    public BidInventoryLog findByIdempotencyKey(String idempotencyKey) {

        BidInventoryLogEntity entity = bidInventoryLogJpaRepository.findByIdempotencyKey(
            idempotencyKey);

        if (entity == null) return null;

        return BidInventoryLogMapper.toDomain(entity);
    }

    @Override
    public BidInventoryLog saveAndFlush(BidInventoryLog log) {
        BidInventoryLogEntity entity = bidInventoryLogJpaRepository.saveAndFlush(BidInventoryLogMapper.toEntityForCreate(log));
        return BidInventoryLogMapper.toDomain(entity);
    }
}
