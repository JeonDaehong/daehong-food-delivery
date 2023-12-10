package com.example.makedelivery.member;

import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.MemberRepository;
import com.example.makedelivery.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

/**
 * 일반적으로는 Test DB 와 연결하여 테스트를 진행합니다.
 */
@SpringBootTest
public class MemberServiceJoinTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입이 정상적으로 진행됩니다.")
    @Transactional
    public void testJoin() {
        // given
        MemberJoinRequest joinRequest = new MemberJoinRequest("aTestAdmin710a@admin.co.kr",
                "1a2a3a4a5a!@#","TestAdmin", MemberLevel.MEMBER);
        Member member = MemberJoinRequest.toEntity(joinRequest, passwordEncoder);
        // when
        memberService.join(joinRequest);
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // then
        assertThat(dbMember.getEmail()).isEqualTo(member.getEmail());

    }

}
