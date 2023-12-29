package com.example.makedelivery.member;

import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.LoginRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.LoginService;
import com.example.makedelivery.domain.member.service.MemberAddressService;
import com.example.makedelivery.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.makedelivery.common.exception.ExceptionEnum.MEMBER_NOT_FOUND;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LoginServiceTest {

    @Autowired
    LoginService loginService;

    @Autowired
    MemberService memberService;

    @Test
    @DisplayName("로그인이 정상적으로 완료됩니다.")
    void loginTest() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("aTestAdmin710a@admin.co.kr")
                .password("1a2a3a4a5a!@#")
                .memberLevel(MemberLevel.MEMBER)
                .build();
        Member member = memberService.findMemberByEmail(loginRequest.getEmail());
        // when
        loginService.loginMember(member.getId());
        // then
        assertThat(loginService.getCurrentMember().getId()).isEqualTo(member.getId());

    }

    @Test
    @DisplayName("로그인 후, 정삭적으로 로그아웃이 됩니다. 로그아웃 된 상태에서는 유저 정보를 조회할 시 MemberNotFoundException 이 발생합니다.")
    void loginAndLogoutTest() {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("aTestAdmin710a@admin.co.kr")
                .password("1a2a3a4a5a!@#")
                .memberLevel(MemberLevel.MEMBER)
                .build();
        Member member = memberService.findMemberByEmail(loginRequest.getEmail());
        // login when
        loginService.loginMember(member.getId());
        // login then
        System.out.println("현재 로그인 한 유저 : " + loginService.getCurrentMember().toString());
        // logout when
        loginService.logoutMember();
        // logout then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            loginService.getCurrentMember();
        });
        assertEquals(MEMBER_NOT_FOUND.getCode(), apiException.getError().getCode());

    }

}
