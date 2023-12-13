package com.example.makedelivery.domain.store.service;

import com.example.makedelivery.common.exception.DuplicateStoreNameException;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.store.model.StoreInfoUpdateRequest;
import com.example.makedelivery.domain.store.model.StoreInsertRequest;
import com.example.makedelivery.domain.store.model.StoreResponse;
import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.model.entity.Store.Status;
import com.example.makedelivery.domain.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private final StoreRepository storeRepository;

    @Transactional
    public boolean addStore(StoreInsertRequest request, Member member) {
        try {
            // 같은 이름의 매장은 등록할 수 없습니다.
            if ( storeRepository.existsByName(request.getName()) ) throw new DuplicateStoreNameException(request.getName());
            Store store = StoreInsertRequest.toEntity(request, member.getId());
            storeRepository.save(store);
            return true;
        } catch ( DuplicateStoreNameException | DuplicateKeyException e ) {
            log.info(e.getMessage());
            return false;
        }
    }

    public void validationCheckedMyStore(Long storeId, Member member) {
        if ( !storeRepository.existsByIdAndOwnerIdAndStatus(storeId, member.getId(), Status.DEFAULT) ) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED); // 401
        }
    }

    public List<StoreResponse> getMyAllStore(Member member) {
        // 상태가 정상인 매장을 이름 순으로 불러옵니다. ( 삭제 된 매장은 불러오지 않습니다. )
        Optional<List<Store>> myStoreListOptional = storeRepository.findAllByOwnerIdAndStatusOrderByName(member.getId(), Status.DEFAULT);
        if ( myStoreListOptional.isPresent() ) {
            List<StoreResponse> storeResponseList = new ArrayList<>();
            List<Store> myStoreList = myStoreListOptional.get();
            for ( Store store : myStoreList ) {
                StoreResponse storeResponse = StoreResponse.toStoreResponse(store);
                storeResponseList.add(storeResponse);
            }
            return storeResponseList;
        }
        return null;
    }

    public StoreResponse getMyStore(Long storeId, Member member) {
        Optional<Store> myStoreOptional = storeRepository.findByIdAndOwnerIdAndStatus(storeId, member.getId(), Status.DEFAULT);
        if ( myStoreOptional.isPresent() ) {
            Store myStore = myStoreOptional.get();
            return StoreResponse.toStoreResponse(myStore);
        }
        return null;
    }

    @Transactional
    public void openMyStore(Long storeId, Member member) {
        Optional<Store> myStoreOptional = storeRepository.findByIdAndOwnerIdAndStatus(storeId, member.getId(), Status.DEFAULT);
        if ( myStoreOptional.isPresent() ) {
            Store myStore = myStoreOptional.get();
            myStore.openStore(LocalDateTime.now());
        }
    }

    @Transactional
    public void closeMyStore(Long storeId, Member member) {
        Optional<Store> myStoreOptional = storeRepository.findByIdAndOwnerIdAndStatus(storeId, member.getId(), Status.DEFAULT);
        if ( myStoreOptional.isPresent() ) {
            Store myStore = myStoreOptional.get();
            myStore.closeStore(LocalDateTime.now());
        }
    }

    @Transactional
    public void updateStoreInformation(Long storeId, StoreInfoUpdateRequest request, Member member) {
        Optional<Store> myStoreOptional = storeRepository.findByIdAndOwnerIdAndStatus(storeId, member.getId(), Status.DEFAULT);
        if ( myStoreOptional.isPresent() ) {
            Store myStore = myStoreOptional.get();
            myStore.updateStoreInfo(request.getName(),
                                    request.getPhone(),
                                    request.getAddress(),
                                    request.getLongitude(),
                                    request.getLatitude(),
                                    request.getIntroduction(),
                                    LocalDateTime.now());
        }
    }

    /**
     * 실제로 DB 에서 바로 삭제되지 않고,
     * Status 가 DEFAULT --> DELETED 로 변경됩니다.
     */
    @Transactional
    public void deleteStore(Long storeId, Member member) {
        Optional<Store> myStoreOptional = storeRepository.findByIdAndOwnerIdAndStatus(storeId, member.getId(), Status.DEFAULT);
        if ( myStoreOptional.isPresent() ) {
            Store myStore = myStoreOptional.get();
            myStore.deleteStoreStatus(LocalDateTime.now());
        }
    }
}
