package com.smore.category.presentation.dto.resopnse;

import com.smore.category.domain.entity.Category;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CategoryResponse {
    private UUID id;
    private String name;
    private String description;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
    }
}
