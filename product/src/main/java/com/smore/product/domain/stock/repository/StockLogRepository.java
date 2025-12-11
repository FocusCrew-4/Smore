package com.smore.product.domain.stock.repository;

import com.smore.product.domain.stock.entity.StockLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StockLogRepository extends JpaRepository<StockLog, UUID> {

    List<StockLog> findByProductIdOrderByCreatedAtDesc(UUID productId);
}