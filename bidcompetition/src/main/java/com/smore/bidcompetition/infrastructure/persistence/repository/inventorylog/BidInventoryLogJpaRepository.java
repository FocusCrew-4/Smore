package com.smore.bidcompetition.infrastructure.persistence.repository.inventorylog;

import com.smore.bidcompetition.infrastructure.persistence.entity.BidInventoryLogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidInventoryLogJpaRepository extends JpaRepository<BidInventoryLogEntity, UUID>,
    BidInventoryLogJpaRepositoryCustom {

}
