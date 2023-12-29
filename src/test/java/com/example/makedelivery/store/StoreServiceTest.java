package com.example.makedelivery.store;

import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.store.model.StoreInsertRequest;
import com.example.makedelivery.domain.store.model.StoreResponse;
import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.model.entity.Store.Status;
import com.example.makedelivery.domain.store.repository.StoreRepository;
import com.example.makedelivery.domain.store.service.StoreService;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreService storeService;

    private StoreInsertRequest storeInsertRequest;

    @BeforeEach
    void setTestStore() {
        storeInsertRequest = StoreInsertRequest.builder()
                .name("테스트 피자집")
                .phone("031-123-5678")
                .address("경기도 수원시 인계동")
                .longitude(37.26587)
                .latitude(127.01015)
                .introduction("맛있는 피자집입니다.")
                .categoryId(1L)
                .build();

    }

    @Test
    @DisplayName("매장 생성에 성공합니다.")
    void addStoreTestSuccess() {
        // given
        Member memberMock = mock(Member.class);
        when(storeRepository.existsByName(storeInsertRequest.getName())).thenReturn(false);
        // when
        storeService.addStore(storeInsertRequest, memberMock);
        // then
        verify(storeRepository, times(1)).save(any(Store.class));
    }

    @Test
    @DisplayName("같은 이름이 있을 경우 매장 생성에 실패하고, Exception 을 반환합니다.")
    void addStoreTestFail() {
        // given
        Member memberMock = mock(Member.class);
        when(storeRepository.existsByName(storeInsertRequest.getName())).thenReturn(true);
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            storeService.addStore(storeInsertRequest, memberMock);
        });
        assertEquals(DUPLICATED_STORE_NAME.getCode(), apiException.getError().getCode());
    }

    @Test
    @DisplayName("점주가 자신의 매장이 맞는지 체크합니다. : 실패 --> Exception 반환")
    void validationCheckedMyStoreSuccess() {
        // given
        Member memberMock = mock(Member.class);
        when(storeRepository.existsByIdAndOwnerIdAndStatus(anyLong(), anyLong(), eq(Status.DEFAULT))).thenReturn(false);
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            storeService.validationCheckedMyStore(1L, memberMock);
        });
        assertEquals(STORE_SECURITY_ERROR.getCode(), apiException.getError().getCode());

    }

    @Test
    @DisplayName("점주가 내 가게 전체 목록을 조회하는데 성공합니다")
    void getMyAllStoreTestSuccess() {
        // given
        Member memberMock = mock(Member.class);
        when(storeRepository.findAllByOwnerIdAndStatusOrderByName(anyLong(), eq(Status.DEFAULT)))
                .thenReturn(Optional.of(new ArrayList<>()));
        // when
        storeService.getMyAllStore(memberMock);
        // then
        verify(storeRepository).findAllByOwnerIdAndStatusOrderByName(anyLong(), eq(Status.DEFAULT));
    }

    @Test
    @DisplayName("가게를 소유하지 않은 점주가 자신의 가게 목록을 조회하면 빈 List 를 반환합니다")
    void getMyAllStoreTestReturnEmptyArray() {
        // given
        Member memberMock = mock(Member.class);
        when(storeRepository.findAllByOwnerIdAndStatusOrderByName(anyLong(), eq(Status.DEFAULT)))
                .thenReturn(Optional.of(Collections.emptyList()));
        // when
        storeService.getMyAllStore(memberMock);
        // then
        verify(storeRepository).findAllByOwnerIdAndStatusOrderByName(anyLong(), eq(Status.DEFAULT));
    }

    @Test
    @DisplayName("점주가 자신의 매장을 열 수 있습니다.")
    void changeStoreOpenStatus() {
        // given
        Member memberMock = mock(Member.class);
        Store storeMock = mock(Store.class);
        when(storeRepository.findByIdAndOwnerIdAndStatus(anyLong(), anyLong(), eq(Status.DEFAULT))).thenReturn(Optional.ofNullable(storeMock));
        // when
        storeService.openMyStore(storeMock.getId(), memberMock);
        // then
        verify(storeMock, times(1)).openStore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("점주가 자신의 매장을 닫을 수 있습니다.")
    void changeStoreCloseStatus() {
        // given
        Member memberMock = mock(Member.class);
        Store storeMock = mock(Store.class);
        when(storeRepository.findByIdAndOwnerIdAndStatus(anyLong(), anyLong(), eq(Status.DEFAULT))).thenReturn(Optional.ofNullable(storeMock));
        // when
        storeService.closeMyStore(storeMock.getId(), memberMock);
        // then
        verify(storeMock, times(1)).closeStore(any(LocalDateTime.class));
    }

    @Test
    @DisplayName("점주가 자신의 매장을 삭제할 수 있습니다. --> Status 가 Delete 로 변경됩니다.")
    void deleteStoreTest() {
        // given
        Member memberMock = mock(Member.class);
        Store storeMock = mock(Store.class);
        when(storeRepository.findByIdAndOwnerIdAndStatus(anyLong(), anyLong(), eq(Status.DEFAULT))).thenReturn(Optional.ofNullable(storeMock));
        // when
        storeService.deleteStore(storeMock.getId(), memberMock);
        // then
        verify(storeMock, times(1)).deleteStoreStatus(any(LocalDateTime.class));
    }
}
