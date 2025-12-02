package com.smore.product.presentation.controller;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.product.application.service.ProductService;
import com.smore.product.presentation.dto.request.CreateProductRequest;
import com.smore.product.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<CommonResponse<ProductResponse>> create(
            @RequestBody CreateProductRequest request
    ) {

        ProductResponse response = productService.createProduct(request);

        return ResponseEntity.ok()
                .body(ApiResponse.ok(response));
    }
}
