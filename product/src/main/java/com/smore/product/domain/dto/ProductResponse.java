package com.smore.product.domain.dto;

import com.smore.product.domain.entity.Product;
import com.smore.product.domain.entity.ProductStatus;
import com.smore.product.domain.entity.SaleType;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductResponse {
    private UUID id;
    private UUID sellerId;
    private UUID categoryId;
    private String name;
    private String description;
    private int price;
    private int stock;
    private SaleType saleType;
    private Integer thresholdForAuction;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductResponse(Product p) {
        this.id = p.getId();
        this.sellerId = p.getSellerId();
        this.categoryId = p.getCategoryId();
        this.name = p.getName();
        this.description = p.getDescription();
        this.price = p.getPrice();
        this.stock = p.getStock();
        this.saleType = p.getSaleType();
        this.thresholdForAuction = p.getThresholdForAuction();
        this.status = p.getStatus();
        this.createdAt = p.getCreatedAt();
        this.updatedAt = p.getUpdatedAt();
    }
}
