package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.domain.member.model.entity.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionLoginService implements LoginService {

    private final MemberService memberService;
    private final HttpSession session;
    public static final String MEMBER_ID = "MEMBER_ID";

    @Override
    public void loginMember(final Long id) {
        session.setAttribute(MEMBER_ID, id);
    }

    @Override
    public void logoutMember() {
        session.removeAttribute(MEMBER_ID);
    }

    @Override
    public Member getCurrentMember() {
        Long memberId = (Long) session.getAttribute(MEMBER_ID);
        return memberService.findMemberById(memberId);
    }

    @Override
    public Long getCurrentMemberId() {
        return (Long) session.getAttribute(MEMBER_ID);
    }


}
