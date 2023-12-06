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

    public boolean existsByEmail(final String email) {
        try {
            if ( memberRepository.existsByEmail(email) ) throw new DuplicatedEmailException();
        } catch ( DuplicatedEmailException e ) {
            log.info(email + " : " + e.getMessage());
            return true;
        }
        return false;
    }

    public Member findMemberByEmail(final String email) {
        return memberRepository.findMemberByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    public Member findMemberById(final Long id) {
        return memberRepository.findMemberById(id).orElseThrow(MemberNotFoundException::new);
    }

    public boolean isValidMember(final LoginRequest request) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(request.getEmail());
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return passwordEncoder.matches(request.getPassword(), member.getPassword()); // true
        }
        return false;
    }

    public boolean isValidPassword(Member member, MemberPasswordRequest passwordRequest) {
        try {
            if ( passwordEncoder.matches(passwordRequest.getOldPassword(), member.getPassword()) ) return true;
            throw new PasswordNotMatchedException();
        } catch ( DuplicatedEmailException e ) {
            log.info(e.getMessage());
            return false;
        }
    }

    @Transactional
    public void updateMemberProfile(final Member member, MemberProfileRequest request) {
        member.updateProfile(request.getNickname());
    }

    @Transactional
    public void updateMemberPassword(Member member, MemberPasswordRequest passwordRequest) {
        member.updatePassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
    }

}
