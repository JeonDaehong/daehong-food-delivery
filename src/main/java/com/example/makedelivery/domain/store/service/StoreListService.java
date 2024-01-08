package com.example.makedelivery.domain.store.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.common.exception.ExceptionEnum;
import com.example.makedelivery.domain.image.service.FileService;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.repository.MemberAddressRepository;
import com.example.makedelivery.domain.store.model.StoreResponse;
import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreListService {

    private final FileService fileService; // AWS S3 Upload & getFileURL

    private final StoreRepository storeRepository;
    private final MemberAddressRepository memberAddressRepository;

    /**
     * 회원이면서, 메인 주소가 등록 되어있는 경우에는 주변 10KM 매장을 검색하여 가까운 순서로 반환 해줍니다.
     * 비회원이거나, 메인 주소를 등록하지 않았을 경우 이름 순서로 반환 해줍니다.
     * <br><br>
     * 또한, 해당 정보를 캐싱하여 담습니다.
     * 캐시에 있는지 확인한 후, 있으면 캐시 메모리에서 가져오고, 없으면(Cache Miss) DB 에서 가져온 후 캐시에 저장합니다.
     * 이러한 방법을 캐시 전략에서 Look Aside 패턴이라고 합니다.
     */
    @Cacheable(key = "#categoryId", value = "storeList")
    @Transactional(readOnly = true)
    public List<StoreResponse> getStoreListByCategory(Member member, Long categoryId) {

        Optional<MemberAddress> memberAddressOptional = memberAddressRepository
                .findTopByStatusAndMemberIdOrderByPriorityAsc(MemberAddress.Status.DEFAULT, member.getId());

        List<Store> stores = memberAddressOptional
                .map(memberAddress ->
                        storeRepository.findAllWithInDistanceOrderByDistance(memberAddress.getLatitude(), memberAddress.getLongitude(), categoryId)
                                .orElse(List.of())
                )
                .orElseGet(() ->
                        storeRepository.findTop30ByCategoryIdOrderByName(categoryId)
                                .orElse(List.of())
                );

        return stores.stream()
                .map(store -> {
                    String awsImagePathURL = fileService.getFilePath(store.getImageFileName());
                    return StoreResponse.toStoreResponse(store, awsImagePathURL);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> getStoreListBySearchName(String searchStoreName) {
        return storeRepository
                .findAllByNameContainingKeywordOrderByName(searchStoreName)
                .orElse(List.of())
                .stream()
                .map(store -> {
                    String awsImagePathURL = fileService.getFilePath(store.getImageFileName());
                    return StoreResponse.toStoreResponse(store, awsImagePathURL);
                })
                .toList();
    }

}
