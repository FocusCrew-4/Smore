package com.smore.order.infrastructure.persistence.repository.order;

import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID>,
    OrderJpaRepositoryCustom {

}
