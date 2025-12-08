package com.smore.product.presentation.controller;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.product.application.service.ProductService;
import com.smore.product.presentation.dto.request.CreateProductRequest;
import com.smore.product.presentation.dto.request.UpdateProductRequest;
import com.smore.product.presentation.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductResponse>> getProduct(
            @PathVariable UUID productId
    ) {
        ProductResponse response = productService.getProduct(productId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<ProductResponse>>> findAll(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<ProductResponse> response = productService.findAll(pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductResponse>> update(
            @PathVariable UUID productId,
            @RequestBody UpdateProductRequest request
    ) {
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
