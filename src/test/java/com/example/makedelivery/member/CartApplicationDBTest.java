package com.example.makedelivery.member;

import com.example.makedelivery.domain.member.model.CartOptionRequest;
import com.example.makedelivery.domain.member.model.CartRequest;
import com.example.makedelivery.domain.member.model.CartResponse;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.CartService;
import com.example.makedelivery.domain.member.service.MemberService;
import com.example.makedelivery.domain.order.domain.OrderMenuOptionRequest;
import com.example.makedelivery.domain.order.domain.OrderMenuRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class CartApplicationDBTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setMember() {
        member = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
    }

    @Test
    @DisplayName("회원가입 한 맴버는 장바구니에 메뉴를 담을 수 있습니다.")
    @Transactional
    void addNewCartTest() {
        // given
        List<CartOptionRequest> optionRequestList = new ArrayList<>();
        CartOptionRequest optionRequest = CartOptionRequest.builder()
                .price(1_000)
                .menuOptionId(1L)
                .menuOptionName("간장 소스")
                .build();
        optionRequestList.add(optionRequest);
        CartRequest request = CartRequest.builder()
                .price(20_000)
                .menuId(1L)
                .menuName("양념 치킨")
                .storeId(1L)
                .count(1)
                .optionList(optionRequestList)
                .build();
        // when
        cartService.addCart(member, request);
        // then
        List<CartResponse> cartResponseList = cartService.loadCart(member);
        for ( int i=0; i<cartResponseList.size(); i++ ) {
            System.out.println("내 장바구니 : " + cartResponseList.get(i).toString());
        }
    }

    @Test
    @DisplayName("같은 옵션, 같은 메뉴를 장바구니에 담으면, Count 가 증가합니다.")
    @Transactional
    void addNewCartCountUpTest() {
        // given
        List<CartOptionRequest> optionRequestList = new ArrayList<>();
        CartOptionRequest optionRequest = CartOptionRequest.builder()
                .price(1_000)
                .menuOptionId(1L)
                .menuOptionName("간장 소스")
                .build();
        optionRequestList.add(optionRequest);
        CartRequest request = CartRequest.builder()
                .price(20_000)
                .menuId(1L)
                .menuName("양념 치킨")
                .storeId(1L)
                .count(1)
                .optionList(optionRequestList)
                .build();
        List<CartOptionRequest> optionRequestList2 = new ArrayList<>();
        CartOptionRequest optionRequest2 = CartOptionRequest.builder()
                .price(1_000)
                .menuOptionId(1L)
                .menuOptionName("간장 소스")
                .build();
        optionRequestList.add(optionRequest2);
        CartRequest request2 = CartRequest.builder()
                .price(20_000)
                .menuId(1L)
                .menuName("양념 치킨")
                .storeId(1L)
                .count(1)
                .optionList(optionRequestList2)
                .build();
        // when
        cartService.addCart(member, request);
        cartService.addCart(member, request2);
        // then ( 카운트가 증가합니다. )
        List<CartResponse> cartResponseList = cartService.loadCart(member);
        assertThat(2).isEqualTo(cartResponseList.get(0).getCount());
    }

}
