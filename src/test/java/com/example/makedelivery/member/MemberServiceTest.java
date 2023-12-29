package com.example.makedelivery.member;

import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.LoginRequest;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.MemberPasswordRequest;
import com.example.makedelivery.domain.member.model.MemberProfileRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.MemberRepository;
import com.example.makedelivery.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ExtendWith 와 MockitoExtension 은 단위 테스트에 공통적으로 사용할 확장 기능을 선언해줍니다.
 * <br><br>
 * Mockito 는 개발자가 동작을 직접 제어할 수 있는 가짜(Mock) 객체를 지원하는 테스트 프레임워크입니다.
 * 일반적으로 Spring 으로 웹 애플리케이션을 개발하면, 여러 객체들 간의 의존성이 생깁니다.
 * 이러한 의존성은 단위 테스트를 작성을 어렵게 하는데,
 * 이를 해결하기 위해 가짜 객체를 주입시켜주는 Mockito 라이브러리를 활용할 수 있습니다.
 * <br><br>
 * InjectMocks 는 @Mock 또는 @Spy 로 생성된 가짜 객체를 자동으로 주입시켜주는 객체입니다.
 * <br><br>
 * Mock 은 실제로 메서드는 갖고 있지만 내부 구현이 없는 상태입니다.
 */
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberJoinRequest memberJoinRequest;
    private LoginRequest loginRequest;
    private MemberProfileRequest memberProfileRequest;
    private MemberPasswordRequest memberPasswordRequest;

    @BeforeEach
    void setTestMember() {

        // Member
        memberJoinRequest = MemberJoinRequest.builder()
                .email("aTestAdmin710a@admin.co.kr")
                .password("1a2a3a4a5a!@#")
                .nickname("TestAdmin")
                .memberLevel(MemberLevel.MEMBER)
                .build();

        loginRequest = LoginRequest.builder()
                .email("aTestAdmin710a@admin.co.kr")
                .password("1a2a3a4a5a!@#")
                .memberLevel(MemberLevel.MEMBER)
                .build();

        memberProfileRequest = MemberProfileRequest.builder()
                .nickname("TestUpdateAdmin")
                .build();

        memberPasswordRequest = MemberPasswordRequest.builder()
                .oldPassword("1a2a3a4a5a!@#")
                .newPassword("1b2b3b4b5b#@!")
                .build();

        member = MemberJoinRequest.toEntity(memberJoinRequest, passwordEncoder);

    }


    @Test
    @DisplayName("중복된 이메일이 존재할 경우 Exception 을 발생시킨다.")
    void existsByEmail() {
        // given
        when(memberRepository.existsByEmail(any())).thenReturn(true);
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            memberService.existsByEmail(member.getEmail());
        });
        assertEquals(DUPLICATED_EMAIL.getCode(), apiException.getError().getCode());
        // System.out.println("apiException.getError().getCode() = " + apiException.getError().getCode());
        // System.out.println("DUPLICATED_EMAIL.getCode() = " + DUPLICATED_EMAIL.getCode());
    }

    @Test
    @DisplayName("해당 이메일로 가입된 사용자가 있는 경우 정상적으로 조회가 된다.")
    void isExistMemberFindByEmail() {
        // given
        when(memberRepository.findMemberByEmail(any())).thenReturn(Optional.of(member));
        // when
        Member findByEmailMember = memberService.findMemberByEmail(member.getEmail());
        // then
        assertThat(findByEmailMember).isNotNull();
        assertThat(findByEmailMember.getId()).isEqualTo(member.getId());
        assertThat(findByEmailMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("해당 이메일로 가입된 사용자가 없는 경우 Exception 을 발생시킨다.")
    void isNotExistMemberFindByEmail() {
        // given
        when(memberRepository.findMemberByEmail(any())).thenReturn(Optional.empty());
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            memberService.findMemberByEmail(member.getEmail());
        });
        assertEquals(MEMBER_NOT_FOUND.getCode(), apiException.getError().getCode());
    }

    @Test
    @DisplayName("로그인 시도 시 패스워드가 불일치하면 Exception 을 발생시킨다.")
    void idValidMemberFalse() {
        // given
        when(memberRepository.findMemberByEmail(any())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            memberService.isValidMember(loginRequest);
        });
        assertEquals(LOGIN_SECURITY_ERROR.getCode(), apiException.getError().getCode());
    }

    @Test
    @DisplayName("사용자의 프로필 ( 닉네임 ) 이 정상적으로 변경된다.")
    void successToUpdateMemberProfile() {
        // when
        memberService.updateMemberProfile(member, memberProfileRequest);
        // then
        assertEquals(member.getNickname(), memberProfileRequest.getNickname());
    }

}
