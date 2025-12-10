package com.smore.product.presentation.dto.request;

import com.smore.product.domain.entity.ProductStatus;
import com.smore.product.domain.entity.SaleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private UUID categoryId;
    private SaleType saleType;
    private Integer thresholdForAuction;
    private ProductStatus status;
}
