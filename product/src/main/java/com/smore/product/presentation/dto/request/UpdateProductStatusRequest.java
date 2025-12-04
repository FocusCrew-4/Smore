package com.smore.product.presentation.dto.request;

import com.smore.product.domain.entity.ProductStatus;
import lombok.Getter;

@Getter
public class UpdateProductStatusRequest {
    private ProductStatus status;
}
