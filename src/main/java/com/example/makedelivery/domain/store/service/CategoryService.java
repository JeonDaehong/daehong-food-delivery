package com.example.makedelivery.domain.store.service;

import com.example.makedelivery.domain.store.model.CategoryResponse;
import com.example.makedelivery.domain.store.model.entity.Category;
import com.example.makedelivery.domain.store.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리는 실제로 몇 개 되지 않으므로, 굳이 캐시에 저장하지 않았습니다.
    // @Cacheable(key = "'categoryList'", value = "categoryList")
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::toCategoryResponse) // 객체 변환
                .collect(Collectors.toList());
    }

}
