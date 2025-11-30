package com.smore.order.domain.vo;

import java.util.UUID;

public record Product(
    UUID productId,
    Integer productPrice
) {
    public Product {
        if (productId == null) throw new IllegalArgumentException("productId는 필수값입니다.");
        if (productPrice == null) throw new IllegalArgumentException("상품 가격은 필수값입니다.");
        if (productPrice < 0) throw new IllegalArgumentException("상품 가격은 음수일 수 없습니다.");
    }
}
