package com.smore.order.infrastructure.persistence.repository.refund;

import com.smore.order.infrastructure.persistence.entity.order.RefundEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundJpaRepository extends JpaRepository<RefundEntity, UUID>,
    RefundJpaRepositoryCustom {

}
