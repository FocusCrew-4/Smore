package com.smore.product.domain.dto;

import com.smore.product.domain.entity.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    private UUID sellerId;
    private UUID categoryId;
    private String name;
    private String description;
    private int price;
    private int stock;
    private SaleType saleType;
    private Integer thresholdForAuction;
}
