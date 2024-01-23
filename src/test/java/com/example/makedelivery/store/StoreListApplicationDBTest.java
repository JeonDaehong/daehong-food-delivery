package com.example.makedelivery.store;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.MemberService;
import com.example.makedelivery.domain.store.model.StoreResponse;
import com.example.makedelivery.domain.store.service.StoreListService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@SpringBootTest
public class StoreListApplicationDBTest {

    @Autowired
    private StoreListService storeListService;

    @Autowired
    private MemberService memberService;

    private Member memberWithAddress;
    private Member memberWithoutAddress;

    @BeforeEach
    void setMember() {
        memberWithAddress = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        memberWithoutAddress = memberService.findMemberByEmail("cTestAdmin710a@admin.co.kr");
    }

    /**
     * Jackson2JsonRedisSerializer 와 GenericJackson2JsonRedisSerializer 의 차이는,
     * Jackson2JsonRedisSerializer 는 직렬화 된 객체 타입을 저장하지 않기에, 역직렬화 시 객체의 타입을 찾기 어렵습니다.
     * GenericJackson2JsonRedisSerializer 는 직렬화시 타입 정보도 함께 저장합니다.
     * 저는 Redis Cache 에서 Value 타입을 저장할 때 직렬화 하는 방법으로 Jackson2JsonRedisSerializer 를 선택하였으므로,
     * 테스트 코드를 아래와 같이 작성하였습니다.
     * ( GenericJackson2JsonRedisSerializer 시 역직렬화 관련 에러가 발생하였는데,
     *   GenericJackson2JsonRedisSerializer 를 유지하며 해결하는 방법을 아직 찾지 못하였습니다.
     *   그래서 Jackson2JsonRedisSerializer 으로 변경하여 해결하였습니다. )
     */
    @Test
    @DisplayName("주소가 저장되어있으면, 근처 10KM 이내의 매장을, 가까운 거리 순으로 가져옵니다. - DB, Redis 의 데이터로 직접 테스트")
    void getStoreListWithAddressTest() {
        // when
        List<StoreResponse> storeResponseList = storeListService.getStoreListByCategory(memberWithAddress, 1L);
        // then
        for ( int i=0; i<storeResponseList.size(); i++ ) {
            System.out.println("근처 매장 : " + storeResponseList.get(i));
        }
    }

    @Test
    @DisplayName("주소가 저장되어 있지 않으면, 이름순으로 정렬하여 매장을 가져옵니다. - Redis 에 캐시 저장이 됩니다.")
    void getStoreListWithoutAddressTest() {
        // when
        List<StoreResponse> storeResponseList = storeListService.getStoreListByCategory(memberWithoutAddress, 1L);
        // then
        for ( int i=0; i<storeResponseList.size(); i++ ) {
            System.out.println("근처 매장 : " + storeResponseList.get(i));
        }
    }

    @Test
    @DisplayName("로그인을 하지 않은 상태라면, 이름순으로 정렬하여 매장을 가져옵니다. - Redis 에 캐시 저장이 되지는 않습니다.")
    void getStoreListNotLoginMemberTest() {
        // when
        List<StoreResponse> storeResponseList = storeListService.getStoreListByCategory(null, 1L);
        // then
        for (StoreResponse storeResponse : storeResponseList) {
            System.out.println("근처 매장 : " + storeResponse);
        }
    }

    @Test
    @DisplayName("검색한 매장의 정보를 가져올 수 있습니다.")
    void getStoreListBySearchTest() {
        // when
        final String SEARCH_WORD = "Test";
        List<StoreResponse> storeResponseList = storeListService.getStoreListBySearchName(SEARCH_WORD);
        // then
        for (StoreResponse storeResponse : storeResponseList) {
            System.out.println("검색 매장 : " + storeResponse);
        }
    }

}
