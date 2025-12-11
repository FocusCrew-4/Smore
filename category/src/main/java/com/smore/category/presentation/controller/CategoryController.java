package com.smore.category.presentation.controller;


import com.smore.category.application.service.CategoryService;
import com.smore.category.presentation.dto.request.CreateCategoryRequest;
import com.smore.category.presentation.dto.request.UpdateCategoryRequest;
import com.smore.category.presentation.dto.resopnse.CategoryResponse;
import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponse>> getCategory(
            @PathVariable UUID id
    ) {
        CategoryResponse response = categoryService.getCategory(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CategoryResponse>>> getCategories() {
        List<CategoryResponse> responses = categoryService.getCategories();
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(
            @PathVariable UUID id,
            @RequestBody UpdateCategoryRequest request
    ) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteCategory(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") Long requesterId
    ) {

        categoryService.deleteCategory(id, requesterId);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}