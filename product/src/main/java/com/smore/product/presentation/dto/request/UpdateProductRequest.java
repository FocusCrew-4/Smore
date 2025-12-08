package com.smore.product.presentation.dto.request;

import com.smore.product.domain.entity.SaleType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateProductRequest {
    private String name;
    private String description;
    private Integer price;
    private Integer stock;
    private UUID categoryId;
    private SaleType saleType;
    private Integer thresholdForAuction;
}
