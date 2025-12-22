package com.smore.product.presentation.controller;

import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import com.smore.product.application.service.ProductSearchService;
import com.smore.product.infrastructure.search.document.ProductDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products/search")
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @GetMapping
    public CommonResponse<List<ProductDocument>> search(
            @RequestParam String keyword
    ) {
        return ApiResponse.ok(
                productSearchService.search(keyword)
        );
    }
}
