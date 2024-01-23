package com.example.makedelivery.store;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.image.service.FileService;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.MemberRepository;
import com.example.makedelivery.domain.member.service.MemberService;
import com.example.makedelivery.domain.store.model.StoreInfoUpdateRequest;
import com.example.makedelivery.domain.store.model.StoreInsertRequest;
import com.example.makedelivery.domain.store.model.StoreResponse;
import com.example.makedelivery.domain.store.model.entity.Store;
import com.example.makedelivery.domain.store.repository.StoreRepository;
import com.example.makedelivery.domain.store.service.StoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.makedelivery.common.exception.ExceptionEnum.MAIN_ADDR_DELETE;
import static com.example.makedelivery.common.exception.ExceptionEnum.STORE_SECURITY_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class StoreApplicationDBTest {

    @Autowired
    private StoreService storeService;

    @Autowired
    private MemberService memberService;

    private Member owner;

    @BeforeEach
    void setMember() {
        owner = memberService.findMemberByEmail("bTestAdmin710a@admin.co.kr");
    }

    @Test
    @DisplayName("자신의 매장이 맞는지 테스트합니다. 틀리면 ApiException(STORE_SECURITY_ERROR) 을 발생시킵니다.")
    @Transactional
    void validationCheckedMyStoreTest() {
        // when
        ApiException apiException = assertThrows(ApiException.class, () -> {
            storeService.validationCheckedMyStore(999L, owner);
        });
        // then
        assertEquals(STORE_SECURITY_ERROR.getCode(), apiException.getError().getCode());
    }

    @Test
    @DisplayName("매장 등록이 정상적으로 진행됩니다 - AWS S3 Test 는 완료되었기 때문에, Store Add 만 테스트합니다.")
    @Transactional
    void addStore() {
        // given
        final String STORE_NAME = "테스트 치킨 매장";
        StoreInsertRequest request = StoreInsertRequest.builder()
                .name(STORE_NAME)
                .phone("010-1234-5678")
                .address("서울 롯데월드 치킨집")
                .introduction("좋은 치킨집입니다.")
                .categoryId(1L)
                .longitude(37.5108467)
                .latitude(127.0975729)
                .build();
        // when
        storeService.addStore(request, owner, "TestImage.png");
        Store dbStore = storeService.findThisStoreName(STORE_NAME);
        // then
        assertThat(dbStore).isNotNull();
        assertThat(STORE_NAME).isEqualTo(dbStore.getName());
    }

    @Test
    @DisplayName("매장 내용이 정상적으로 수정되어야 합니다.")
    @Transactional
    void updateStore() {
        // given
        final String UPDATE_STORE_NAME = "바뀐 테스트 치킨 매장";
        StoreInfoUpdateRequest request = StoreInfoUpdateRequest.builder()
                .name(UPDATE_STORE_NAME)
                .phone("010-1234-5678")
                .address("서울 롯데월드 치킨집")
                .introduction("좋은 치킨집입니다.")
                .longitude(37.5108467)
                .latitude(127.0975729)
                .build();
        // when
        storeService.updateStoreInformation(1L, request, owner);
        Store dbStore = storeService.findThisStore(1L);
        // then
        assertThat(UPDATE_STORE_NAME).isEqualTo(dbStore.getName());

    }

    @Test
    @DisplayName("매장을 열고 닫을 수 있어야 합니다.")
    void changeStoreOpenStatus() {
        // open when
        storeService.openMyStore(1L, owner);
        Store dbStore = storeService.findThisStore(1L);
        // open then
        assertThat(Store.OpenStatus.OPENED).isEqualTo(dbStore.getOpenStatus());
        // close when
        storeService.closeMyStore(1L, owner);
        dbStore = storeService.findThisStore(1L);
        // close then
        assertThat(Store.OpenStatus.CLOSED).isEqualTo(dbStore.getOpenStatus());
    }

    @Test
    @DisplayName("점주가 등록한 모든 매장 정보를 가져옵니다. - Test DB로 테스트 진행")
    @Transactional
    void getMyAllStoreTest() {
        // given
        final String STORE_NAME = "테스트 치킨 매장";
        StoreInsertRequest request = StoreInsertRequest.builder()
                .name(STORE_NAME)
                .phone("010-1234-5678")
                .address("서울 롯데월드 치킨집")
                .introduction("좋은 치킨집입니다.")
                .categoryId(1L)
                .longitude(37.5108467)
                .latitude(127.0975729)
                .build();
        // when
        storeService.addStore(request, owner, "TestImage.png");
        // then ( System.out.println )
        storeService.getMyAllStore(owner)
                .forEach(response -> System.out.println("보유 매장 정보 : " + response.toString()));
    }

}
