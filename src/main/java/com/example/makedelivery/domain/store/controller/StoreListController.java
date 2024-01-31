package com.example.makedelivery.domain.store.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.store.model.CategoryResponse;
import com.example.makedelivery.domain.store.model.StoreResponse;
import com.example.makedelivery.domain.store.service.CategoryService;
import com.example.makedelivery.domain.store.service.StoreListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.makedelivery.common.constants.URIConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(STORE_LIST_API_URI)
public class StoreListController {

    private final CategoryService categoryService;
    private final StoreListService storeListService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> categoryList = categoryService.getCategories();
        return ResponseEntity.status(HttpStatus.OK).body(categoryList);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<List<StoreResponse>> getStoreListByCategory(@RequestParam(required = false) Long memberId,
                                                                      @PathVariable Long categoryId) {
        List<StoreResponse> storeList = storeListService.getStoreListByCategory(memberId, categoryId);
        return ResponseEntity.status(HttpStatus.OK).body(storeList);
    }

    @GetMapping("/searchStoreName/{searchStoreName}")
    public ResponseEntity<List<StoreResponse>> getStoreListByCategory(@PathVariable String searchStoreName) {
        List<StoreResponse> storeList = storeListService.getStoreListBySearchName(searchStoreName);
        return ResponseEntity.status(HttpStatus.OK).body(storeList);
    }

}
