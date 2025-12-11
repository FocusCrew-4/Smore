package com.smore.product.domain.stock.dto;

import com.smore.product.domain.stock.entity.StockLog;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class StockLogResponse {

    private final UUID id;
    private final int beforeStock;
    private final int afterStock;
    private final LocalDateTime createdAt;

    public StockLogResponse(StockLog log) {
        this.id = log.getId();
        this.beforeStock = log.getBeforeStock();
        this.afterStock = log.getAfterStock();
        this.createdAt = log.getCreatedAt();
    }
}