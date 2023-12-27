package com.example.makedelivery.domain.store.model;

import com.example.makedelivery.domain.store.model.entity.Category;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryResponse {

    private Long id;
    private String name;

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

}
