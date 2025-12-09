package com.smore.category.presentation.controller;


import com.smore.category.application.service.CategoryService;
import com.smore.category.presentation.dto.request.CreateCategoryRequest;
import com.smore.category.presentation.dto.resopnse.CategoryResponse;
import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CommonResponse<CategoryResponse>> create(
            @RequestBody CreateCategoryRequest request
    ) {
        CategoryResponse response = categoryService.create(request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}