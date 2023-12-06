package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.domain.member.model.entity.Member;

/**
 * 로그인 하는 로직은 여러 Controller에서 사용할 수 있기 때문에 따로 Service로 구현하였습니다.
 * 현재는 Session 방식이지만, 추후 토큰 등 여러 형태로 구현이 가능하기 때문에
 * Interface로 만들어 컨트롤러가 로그인 서비스를 간접적으로 의존하도록 하였습니다.
 * 컨트롤러는 LoginService가 Session으로 구현하였든, 토큰으로 구현하였든 알 필요가 없습니다.
 */
public interface LoginService {
    void loginMember(final Long id);

    void logoutMember();

    Member getCurrentMember();

    Long getCurrentMemberId();
}
