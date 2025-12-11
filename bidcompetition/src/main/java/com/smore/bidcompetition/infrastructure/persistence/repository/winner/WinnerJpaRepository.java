package com.smore.bidcompetition.infrastructure.persistence.repository.winner;

import com.smore.bidcompetition.infrastructure.persistence.entity.WinnerEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnerJpaRepository extends JpaRepository<WinnerEntity, UUID>,
    WinnerJpaRepositoryCustom {

}
