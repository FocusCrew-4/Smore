package com.smore.category.application.service;

import com.smore.category.domain.entity.Category;
import com.smore.category.domain.repository.CategoryRepository;
import com.smore.category.presentation.dto.request.CreateCategoryRequest;
import com.smore.category.presentation.dto.request.UpdateCategoryRequest;
import com.smore.category.presentation.dto.resopnse.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {

        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 카테고리입니다.");
        }

        Category category = Category.create(
                request.getName(),
                request.getDescription()
        );

        categoryRepository.save(category);

        return new CategoryResponse(category);
    }

    public CategoryResponse getCategory(UUID id) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        return new CategoryResponse(category);
    }

    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(CategoryResponse::new)
                .toList();
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest req) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        category.update(req.getName(), req.getDescription());

        return new CategoryResponse(category);
    }

    @Transactional
    public void deleteCategory(UUID id, UUID requesterId) {

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        category.softDelete(requesterId);
    }
}
