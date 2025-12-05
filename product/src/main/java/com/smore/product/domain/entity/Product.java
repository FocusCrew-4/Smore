package com.smore.product.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Clock;
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

    public static Product create(
            Long sellerId,
            UUID categoryId,
            String name,
            String description,
            int price,
            int stock,
            SaleType saleType,
            Integer thresholdForAuction
    ) {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        return Product.builder()
                .sellerId(sellerId)
                .categoryId(categoryId)
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .saleType(saleType)
                .thresholdForAuction(thresholdForAuction)
                .status(ProductStatus.ON_SALE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void update(
            String name,
            String description,
            Integer price,
            Integer stock,
            UUID categoryId,
            SaleType saleType,
            Integer thresholdForAuction
    ) {
        if (name != null) this.name = name;
        if (description != null) this.description = description;
        if (price != null) this.price = price;
        if (stock != null) this.stock = stock;
        if (categoryId != null) this.categoryId = categoryId;
        if (saleType != null) this.saleType = saleType;
        if (thresholdForAuction != null) this.thresholdForAuction = thresholdForAuction;

        this.updatedAt = LocalDateTime.now(Clock.systemUTC());
    }

    public void softDelete(Long requesterId) {
        this.status = ProductStatus.INACTIVE;
        this.deletedAt = LocalDateTime.now(Clock.systemUTC());
        this.deletedBy = requesterId;
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
}
