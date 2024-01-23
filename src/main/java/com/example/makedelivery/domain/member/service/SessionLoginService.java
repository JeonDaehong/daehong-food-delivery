package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.MemberRedisCacheRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
  *  레디스의 키값으로 저장되는 타입입니다.
 */
@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

    private final MemberService memberService;
    private final HttpSession session;

    private final MemberRedisCacheRepository memberRedisCacheRepository;

    public static final String MEMBER_ID = "MEMBER_ID";

    @Override
    public void loginMember(final Long id) {
        session.setAttribute(MEMBER_ID, id);
    }

    @Override
    public void logoutMember(Member member) {
        // 로그인 중에 쌓인 해당 Member 의 캐시 전부 삭제
        memberRedisCacheRepository.evictCachesByMember(member.getId());
        session.removeAttribute(MEMBER_ID);
    }

    @Override
    public Member getCurrentMember() {
        // Redis 에서 Key 가 String 타입으로 직렬화, 역직렬화를 구현하였기 때문에
        // 먼저 String 으로 만들어 준 후 Long 으로 형변환을 해줘야 합니다.
        Long memberId = Long.parseLong(String.valueOf(session.getAttribute(MEMBER_ID)));
        return memberService.findMemberById(memberId);
    }

    @Override
    public Long getCurrentMemberId() {
        return Long.parseLong(String.valueOf(session.getAttribute(MEMBER_ID)));
    }


}
