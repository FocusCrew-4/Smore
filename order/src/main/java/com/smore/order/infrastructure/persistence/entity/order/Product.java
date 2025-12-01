package com.smore.order.infrastructure.persistence.entity.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "product_price", nullable = false)
    private Integer productPrice;

    protected Product(UUID productId, Integer productPrice) {
        if (productId == null) throw new IllegalArgumentException("상품의 아이디는 필수입니다.");
        if (productPrice == null) throw new IllegalArgumentException("상품 가격은 필수값입니다.");
        if (productPrice < 0) throw new IllegalArgumentException("상품 가격은 음수일 수 없습니다.");
        this.productId = productId;
        this.productPrice = productPrice;
    }

}
