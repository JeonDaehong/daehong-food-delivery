package com.example.makedelivery.common.aop;

import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginCheckAspect {

    private final LoginService loginService;

    @Before("@annotation(com.example.makedelivery.common.annotation.LoginCheck) && @annotation(target)")
    public void loginCheck(LoginCheck target) throws HttpClientErrorException {

        Member currentMember = getCurrentMember();

        switch (target.memberLevel()) {
            case MEMBER -> checkMemberLevel(currentMember, MemberLevel.MEMBER);
            case OWNER -> checkMemberLevel(currentMember, MemberLevel.OWNER);
            case RIDER -> checkMemberLevel(currentMember, MemberLevel.RIDER);
        }
    }

    private Member getCurrentMember() throws HttpClientErrorException {
        Member member = loginService.getCurrentMember();
        if (member == null) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
        return member;
    }

    private void checkMemberLevel(Member currentMember, MemberLevel requiredLevel) {
        if (!(currentMember.getMemberLevel() == requiredLevel)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED);
        }
    }
}
