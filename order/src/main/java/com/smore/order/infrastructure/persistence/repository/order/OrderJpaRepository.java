package com.smore.order.infrastructure.persistence.repository.order;

import com.smore.order.infrastructure.persistence.entity.order.OrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID>,
    OrderJpaRepositoryCustom {

    @Query(
    value ="""
        SELECT * 
        FROM p_order 
        WHERE id = :id
    """,
    nativeQuery = true)
    OrderEntity findByIdIncludingDeleted(@Param("id") UUID orderId);
}
