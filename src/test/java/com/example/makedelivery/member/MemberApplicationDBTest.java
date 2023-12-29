package com.example.makedelivery.member;

import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.MemberAddressRequest;
import com.example.makedelivery.domain.member.model.MemberAddressResponse;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.MemberProfileRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.repository.MemberAddressRepository;
import com.example.makedelivery.domain.member.service.MemberAddressService;
import com.example.makedelivery.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.makedelivery.common.exception.ExceptionEnum.LOGIN_SECURITY_ERROR;
import static com.example.makedelivery.common.exception.ExceptionEnum.MAIN_ADDR_DELETE;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test DB 와 연결하여 테스트를 진행합니다.
 */
@SpringBootTest
public class MemberApplicationDBTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    MemberAddressService memberAddressService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입이 정상적으로 진행됩니다.")
    @Transactional
    void testJoin() {
        // given
        MemberJoinRequest joinRequest = MemberJoinRequest.builder()
                .email("aTestAdmin710a@admin.co.kr")
                .password("1a2a3a4a5a!@#")
                .nickname("TestAdmin")
                .memberLevel(MemberLevel.MEMBER)
                .build();
        Member member = MemberJoinRequest.toEntity(joinRequest, passwordEncoder);
        // when
        memberService.join(joinRequest);
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // then
        assertThat(dbMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("프로필 업데이트가 정상적으로 진행됩니다.")
    @Transactional
    void testUpdateProfile() {
        // given
        MemberProfileRequest profileRequest = MemberProfileRequest.builder().nickname("TestAdminUpdate").build();
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when
        memberService.updateMemberProfile(dbMember, profileRequest);
        // then
        assertThat(dbMember.getNickname()).isEqualTo(profileRequest.getNickname());
    }

    @Test
    @DisplayName("회원 정보가 정상적으로 삭제되고, 다시 재조회 시 Status 가 DELETE 로 출력되야 합니다.")
    @Transactional
    void testDelete() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when
        memberService.deleteMember(dbMember);
        // then
        dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        assertThat(dbMember.getStatus()).isEqualTo(Member.Status.DELETED);
    }

    @Test
    @DisplayName("주소를 생성이 정상적으로 진행되고, 메인 주소가 없을 경우 메인 주소로 등록합니다.")
    @Transactional
    @Rollback(value = false)
    void testAddAddress() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        MemberAddressRequest request = MemberAddressRequest.builder()
                .address("서울 강남구 대치동 123번길 77")
                .longitude(32.1257)
                .latitude(102.7571)
                .build();
        // when
        memberAddressService.addAddress(dbMember, request);
        // then
        dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        assertFalse(memberAddressService.getMyAddressList(dbMember).isEmpty());
        assertNotNull(dbMember.getMainAddressId());
    }

    @Test
    @DisplayName("회원의 메인 주소 ID가 정상적으로 변경되는지 확인합니다.")
    @Transactional
    void testUpdateMainAddress() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when
        dbMember.updateMainAddress(999L, LocalDateTime.now());
        // then
        Member reDbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        assertThat(999L).isEqualTo(reDbMember.getMainAddressId());
    }

    @Test
    @DisplayName("회원의 메인주소로 지정된 주소를 삭제하면 Exception 을 발생시킵니다.")
    @Transactional
    void deleteMainAddressThrowsException() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            memberAddressService.deleteAddress(dbMember, 1L);
        });
        assertEquals(MAIN_ADDR_DELETE.getCode(), apiException.getError().getCode());
    }

}
