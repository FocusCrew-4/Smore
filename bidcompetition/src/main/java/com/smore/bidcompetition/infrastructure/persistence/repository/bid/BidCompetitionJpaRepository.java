package com.smore.bidcompetition.infrastructure.persistence.repository.bid;

import com.smore.bidcompetition.infrastructure.persistence.entity.BidCompetitionEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidCompetitionJpaRepository extends JpaRepository<BidCompetitionEntity, UUID>,
    BidCompetitionJpaRepositoryCustom {

}
