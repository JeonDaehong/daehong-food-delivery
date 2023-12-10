package com.example.makedelivery.member;

import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.common.exception.MemberNotFoundException;
import com.example.makedelivery.domain.member.model.LoginRequest;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.MemberPasswordRequest;
import com.example.makedelivery.domain.member.model.MemberProfileRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.MemberRepository;
import com.example.makedelivery.domain.member.service.MemberService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Member member;
    private MemberJoinRequest memberJoinRequest;
    private LoginRequest loginRequest;
    private MemberProfileRequest memberProfileRequest;
    private MemberPasswordRequest memberPasswordRequest;

    private final String TEST_EMAIL = "aTestAdmin710a@admin.co.kr";

    @BeforeEach
    void setTestMember() {

        // Member
        memberJoinRequest = new MemberJoinRequest(TEST_EMAIL,
                "1a2a3a4a5a!@#","TestAdmin", MemberLevel.MEMBER);

        loginRequest = new LoginRequest(TEST_EMAIL, "1a2a3a4a5a!@#", MemberLevel.MEMBER);

        memberProfileRequest = new MemberProfileRequest("TestUpdateAdmin");

        memberPasswordRequest = new MemberPasswordRequest("1a2a3a4a5a!@#", "1b2b3b4b5b#@!");

        member = MemberJoinRequest.toEntity(memberJoinRequest, passwordEncoder);

    }


    @Test
    @DisplayName("중복된 이메일이 존재할 경우 true 를 반환합니다.")
    void existsByEmail() {
        // given & when
        when(memberRepository.existsByEmail(any())).thenReturn(true);
        // then
        assertTrue(memberService.existsByEmail(member.getEmail()));
    }

    @Test
    @DisplayName("중복된 이메일이 없을 경우 false 를 반환합니다.")
    void notExistsByEmail() {
        // given & when
        when(memberRepository.existsByEmail(any())).thenReturn(false);
        // then
        assertFalse(memberService.existsByEmail(member.getEmail()));
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
    @DisplayName("해당 이메일로 가입된 사용자가 없는 경우 에러를 발생한다.")
    void isNotExistMemberFindByEmail() {
        // given & when
        when(memberRepository.findMemberByEmail(any())).thenReturn(Optional.empty());
        // then
        assertThrows(MemberNotFoundException.class, () -> { memberService.findMemberByEmail(member.getEmail()); });
    }

    @Test
    @DisplayName("로그인 시도 시 패스워드가 일치하면 true 를 반환한다.")
    void idValidMemberTrue() {
        // given
        when(memberRepository.findMemberByEmail(any())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        // then
        assertTrue(memberService.isValidMember(loginRequest));
    }

    @Test
    @DisplayName("로그인 시도 시 패스워드가 불일치하면 false 를 반환한다.")
    void idValidMemberFalse() {
        // given
        when(memberRepository.findMemberByEmail(any())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        // then
        assertFalse(memberService.isValidMember(loginRequest));
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
