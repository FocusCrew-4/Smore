package com.smore.category.presentation.dto.request;

import lombok.Getter;

@Getter
public class CreateCategoryRequest {
    private String name;
    private String description;
}