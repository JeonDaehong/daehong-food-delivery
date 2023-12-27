package com.example.makedelivery.domain.store.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.store.model.StoreInfoUpdateRequest;
import com.example.makedelivery.domain.store.model.StoreInsertRequest;
import com.example.makedelivery.domain.store.model.StoreResponse;
import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.model.entity.Store.Status;
import com.example.makedelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;

    private Store findMyStore(Long storeId, Member member) {
        return storeRepository.findByIdAndOwnerIdAndStatus(storeId, member.getId(), Status.DEFAULT)
                .orElseThrow(() -> new ApiException(STORE_NOT_FOUND));
    }

    @Transactional
    public void addStore(StoreInsertRequest request, Member member) {
        // 같은 이름의 매장은 등록할 수 없습니다.
        if ( storeRepository.existsByName(request.getName()) ) throw new ApiException(DUPLICATED_STORE_NAME);
        Store store = StoreInsertRequest.toEntity(request, member.getId());
        storeRepository.save(store);
    }

    @Transactional(readOnly = true)
    public void validationCheckedMyStore(Long storeId, Member member) {
        if ( !storeRepository.existsByIdAndOwnerIdAndStatus(storeId, member.getId(), Status.DEFAULT) ) {
            throw new ApiException(STORE_SECURITY_ERROR); // 401
        }
    }

    @Transactional(readOnly = true)
    public List<StoreResponse> getMyAllStore(Member member) {
        // 상태가 정상인 매장을 이름 순으로 불러옵니다. ( 삭제 된 매장은 불러오지 않습니다. )
        return storeRepository
                .findAllByOwnerIdAndStatusOrderByName(member.getId(), Status.DEFAULT)
                .orElse(List.of())
                .stream()
                .map(StoreResponse::toStoreResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public StoreResponse getMyStore(Long storeId, Member member) {
        Store myStore = findMyStore(storeId, member);
        return StoreResponse.toStoreResponse(myStore);

    }

    @Transactional
    public void openMyStore(Long storeId, Member member) {
        Store myStore = findMyStore(storeId, member);
        myStore.openStore(LocalDateTime.now());
    }

    @Transactional
    public void closeMyStore(Long storeId, Member member) {
        Store myStore = findMyStore(storeId, member);
        myStore.closeStore(LocalDateTime.now());
    }

    @Transactional
    public void updateStoreInformation(Long storeId, StoreInfoUpdateRequest request, Member member) {
        Store myStore = findMyStore(storeId, member);
        myStore.updateStoreInfo(request.getName(),
                                request.getPhone(),
                                request.getAddress(),
                                request.getLongitude(),
                                request.getLatitude(),
                                request.getIntroduction(),
                                LocalDateTime.now());
    }

    /**
     * 실제로 DB 에서 바로 삭제되지 않고,
     * Status 가 DEFAULT --> DELETED 로 변경됩니다.
     * <br><br>
     * 그리고 이 때 DB의 정보가 바뀌게 되므로,
     * 고객들의 StoreList 관련 된 캐싱 정보를 모두 초기화 합니다.
     */
    @Transactional
    @CacheEvict(value = "storeList", allEntries = true)
    public void deleteStore(Long storeId, Member member) {
        Store myStore = findMyStore(storeId, member);
        myStore.deleteStoreStatus(LocalDateTime.now());
    }

    @Transactional
    public void deleteAllStoreDeleteStatus() {
        storeRepository.deleteAllByStatus(Status.DELETED);
    }
}
