package com.smore.product.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder

public class Product {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    private Long sellerId;

    private UUID categoryId;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private int price;

    private int stock;

    @Enumerated(EnumType.STRING)
    private SaleType saleType;

    private Integer thresholdForAuction;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

    @PrePersist
    public void onCreate() {
        this.status = ProductStatus.ON_SALE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changePrice(Integer price) {
        this.price = price;
    }

    public void changeStock(Integer stock) {
        this.stock = stock;
    }

    public void changeCategory(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public void changeSaleType(SaleType saleType) {
        this.saleType = saleType;
    }

    public void changeThreshold(Integer threshold) {
        this.thresholdForAuction = threshold;
    }

    public void changeStatus(ProductStatus status) {
        this.status = status;
    }
}
