package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.common.exception.DuplicatedEmailException;
import com.example.makedelivery.common.exception.MemberNotFoundException;
import com.example.makedelivery.common.exception.PasswordNotMatchedException;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.LoginRequest;
import com.example.makedelivery.domain.member.model.MemberPasswordRequest;
import com.example.makedelivery.domain.member.model.MemberProfileRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public void join(final MemberJoinRequest memberJoinRequest) {
        Member member = MemberJoinRequest.toEntity(memberJoinRequest, passwordEncoder);
        memberRepository.save(member);
    }

    public boolean existsByEmail(String email) {
        try {
            if ( memberRepository.existsByEmail(email) ) throw new DuplicatedEmailException();
        } catch ( DuplicatedEmailException e ) {
            log.info(email + " : " + e.getMessage());
            return true;
        }
        return false;
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findMemberByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    public Member findMemberById(Long id) {
        return memberRepository.findMemberById(id).orElseThrow(MemberNotFoundException::new);
    }

    public boolean isValidMember(LoginRequest request) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(request.getEmail());
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return passwordEncoder.matches(request.getPassword(), member.getPassword()); // true
        }
        return false;
    }

    public boolean isValidPassword(Member member, String inputPassword) {
        try {
            if ( passwordEncoder.matches(inputPassword, member.getPassword()) ) return false;
            throw new PasswordNotMatchedException();
        } catch ( DuplicatedEmailException e ) {
            log.info(e.getMessage());
            return true;
        }
    }

    @Transactional
    public void updateMemberProfile(Member member, MemberProfileRequest request) {
        member.updateProfile(request.getNickname());
    }

    @Transactional
    public void updateMemberPassword(Member member, MemberPasswordRequest passwordRequest) {
        member.updatePassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
    }

    /**
     * 실제 현업이라면,
     * DB 에서 테이블 별로 묶여있는 외래키를 고려하여 DEL_YN 과 같은 컬럼을 활용하여
     * 해당 회원이 탈퇴하여도, 엮어있는 다른 테이블에 문제가 생기지 않게 하거나,
     * CASCADE 를 활용하여 연쇄 삭제를 해야하는 등의 설계 & 구현을 해야합니다.
     */
    @Transactional
    public void deleteMember(Member member) {
        memberRepository.deleteMemberById(member.getId());
    }

}
