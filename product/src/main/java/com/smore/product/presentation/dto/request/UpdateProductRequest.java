package com.smore.product.presentation.dto.request;

import com.smore.product.domain.entity.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
@NoArgsConstructor
@AllArgsConstructor
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
