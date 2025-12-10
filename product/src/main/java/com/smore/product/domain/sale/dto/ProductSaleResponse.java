package com.smore.product.domain.sale.dto;

import com.smore.product.domain.sale.entity.ProductSale;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ProductSaleResponse {

    private final UUID id;
    private final int quantity;
    private final BigDecimal priceAtPurchase;
    private final LocalDateTime createdAt;

    public ProductSaleResponse(ProductSale sale) {
        this.id = sale.getId();
        this.quantity = sale.getQuantity();
        this.priceAtPurchase = sale.getPriceAtPurchase();
        this.createdAt = sale.getCreatedAt();
    }
}