package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.*;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.model.entity.MemberAddress.Status;
import com.example.makedelivery.domain.member.repository.MemberAddressRepository;
import com.example.makedelivery.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;

/**
 * 확장 가능성이 없으므로 interface를 구현하지 않았습니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(MemberJoinRequest memberJoinRequest) {
        Member member = MemberJoinRequest.toEntity(memberJoinRequest, passwordEncoder);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public void existsByEmail(String email) {
        if ( memberRepository.existsByEmail(email) ) throw new ApiException(DUPLICATED_EMAIL);
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email).orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Member findMemberById(Long id) {
        return memberRepository.findMemberById(id).orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public void isValidStatus(String email) {

        Member member = memberRepository.findMemberByEmail(email)
                .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));

        if ( member.getStatus() == Member.Status.DELETED ) throw new ApiException(MEMBER_STATUS_DELETE); // 삭제 처리중인지
        if ( member.getStatus() == Member.Status.STOPPED ) throw new ApiException(MEMBER_STATUS_STOPPED); // 정지된 상태인지
    }

    @Transactional(readOnly = true)
    public void isValidMember(LoginRequest request) {

        Member member = memberRepository.findMemberByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(MEMBER_NOT_FOUND));

        if ( ! passwordEncoder.matches(request.getPassword(), member.getPassword()) ) throw new ApiException(LOGIN_SECURITY_ERROR);
    }

    @Transactional(readOnly = true)
    public void isValidPassword(Member member, String inputPassword) {
        if ( ! passwordEncoder.matches(inputPassword, member.getPassword()) ) throw new ApiException(PASSWORD_NOT_MATCHED);
    }

    @Transactional
    public void updateMemberProfile(Member member, MemberProfileRequest request) {
        member.updateProfile(request.getNickname());
    }

    @Transactional
    public void updateMemberPassword(Member member, MemberPasswordRequest passwordRequest) {
        member.updatePassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
    }

    @Transactional
    public void convertPointsToAvailablePoints(Member member, int desiredChangePoints) {
        if ( member.getPoint() < desiredChangePoints ) throw new ApiException(POINTS_INSUFFICIENT); // 전환하려는 포인트보다 보유포인트가 적으면 예외발생
        if ( desiredChangePoints % 5_000 != 0 || desiredChangePoints < 5_000 ) throw new ApiException(INVALID_POINT_UNIT); // 포인트 전환 단위는 최소 5,000원이며, 5,000원 단위로만 전환 가능.
        member.convertPointsToAvailablePoints(desiredChangePoints);
    }

    /**
     * DB 에서 테이블 별로 묶여있는 외래키를 고려하여 Status 와 같은 컬럼을 활용하여
     * 해당 회원이 탈퇴하여도, 엮어있는 다른 테이블에 문제가 생기지 않게 하거나,
     * CASCADE 를 활용하여 연쇄 삭제를 해야하는 등의 설계 & 구현을 해야합니다.
     */
    @Transactional
    public void deleteMember(Member member) {
        member.deleteMemberStatus();
    }

}
