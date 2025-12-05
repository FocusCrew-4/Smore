package com.smore.product.presentation.dto.request;

import com.smore.product.domain.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateProductStatusRequest {
    private ProductStatus status;
}
