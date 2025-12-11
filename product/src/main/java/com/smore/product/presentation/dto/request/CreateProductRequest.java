package com.smore.product.presentation.dto.request;

import com.smore.product.domain.entity.ProductStatus;
import com.smore.product.domain.entity.SaleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @NotNull(message = "sellerId는 필수입니다.")
    private Long sellerId;

    @NotNull(message = "categoryId는 필수입니다.")
    private UUID categoryId;

    @NotBlank(message = "상품명은 필수입니다.")
    private String name;

    @NotBlank(message = "상품 설명은 필수입니다.")
    private String description;

    @Positive(message = "가격은 0보다 큰 값이어야 합니다.")
    private BigDecimal price;

    @PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
    private int stock;

    @NotNull(message = "판매 유형은 필수입니다.")
    private SaleType saleType;

    @NotNull(message = "상품 판매 상태는 필수입니다.")
    private ProductStatus status;

    // LIMITED_TO_AUCTION일 때는 서비스에서 추가 검증
    @Min(value = 1, message = "경매 전환 기준은 1 이상이어야 합니다.")
    private Integer thresholdForAuction;

    private String startAt;

    private String endAt;
}
