package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.common.exception.DuplicatedEmailException;
import com.example.makedelivery.common.exception.MainAddressDeletionException;
import com.example.makedelivery.common.exception.MemberNotFoundException;
import com.example.makedelivery.common.exception.PasswordNotMatchedException;
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

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 확장 가능성이 없으므로 interface를 구현하지 않았습니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberAddressRepository memberAddressRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(MemberJoinRequest memberJoinRequest) {
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

    public boolean isValidStatus(String email) {
        Optional<Member> memberOptional = memberRepository.findMemberByEmail(email);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return member.getStatus() == Member.Status.DEFAULT;
        }
        return false;
    }

    public boolean isValidStatus(Long memberId) {
        Optional<Member> memberOptional = memberRepository.findMemberById(memberId);
        if (memberOptional.isPresent()) {
            Member member = memberOptional.get();
            return member.getStatus() == Member.Status.DEFAULT;
        }
        return false;
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
        member.updateProfile(request.getNickname(), LocalDateTime.now());
    }

    @Transactional
    public void updateMemberPassword(Member member, MemberPasswordRequest passwordRequest) {
        member.updatePassword(passwordEncoder.encode(passwordRequest.getNewPassword()), LocalDateTime.now());
    }

    /**
     * DB 에서 테이블 별로 묶여있는 외래키를 고려하여 Status 와 같은 컬럼을 활용하여
     * 해당 회원이 탈퇴하여도, 엮어있는 다른 테이블에 문제가 생기지 않게 하거나,
     * CASCADE 를 활용하여 연쇄 삭제를 해야하는 등의 설계 & 구현을 해야합니다.
     */
    @Transactional
    public void deleteMember(Member member) {
        member.deleteMemberStatus(LocalDateTime.now());
    }

    @Transactional
    public void addAddress(Member member, MemberAddressRequest request) {
        MemberAddress address = MemberAddressRequest.toEntity(request, member.getId());
        Long addressId = memberAddressRepository.save(address).getId();
        // 아직 메인 주소가 등록되어있지 않다면, 자동으로 메인 주소를 등록해준다.
        if (member.getMainAddressId() == null) member.updateMainAddress(addressId, LocalDateTime.now());
    }

    @Transactional
    public void updateMemberAddress(Member member, Long addressId) {
        member.updateMainAddress(addressId, LocalDateTime.now());
    }

    @Transactional
    public boolean deleteAddress(Member member, Long addressId) {
        try {
            // 메인 주소는 삭제할 수 없습니다.
            if (Objects.equals(member.getMainAddressId(), addressId)) throw new MainAddressDeletionException();
            Optional<MemberAddress> memberAddressOptional = memberAddressRepository.findMemberAddressesByIdAndMemberId(addressId, member.getId());
            if (memberAddressOptional.isPresent()) {
                MemberAddress memberAddress = memberAddressOptional.get();
                memberAddress.deleteAddress(LocalDateTime.now());
                return true;
            }
            return false;
        } catch ( MainAddressDeletionException e ) {
            log.info(e.getMessage());
            return false;
        }
    }

    public List<MemberAddressResponse> getMyAddressList(Member member) {
        // Status 가 DEFAULT 인 주소만 불러옵니다.
        Optional<List<MemberAddress>> memberAddressListOptional = memberAddressRepository.findAllByMemberIdAndStatus(member.getId(), Status.DEFAULT);
        if ( memberAddressListOptional.isPresent() ) {
            List<MemberAddress> memberAddressList = memberAddressListOptional.get();
            List<MemberAddressResponse> memberAddressResponseList = new ArrayList<>();
            for ( MemberAddress memberAddress : memberAddressList ) {
                MemberAddressResponse memberAddressResponse = MemberAddressResponse.toMemberAddressResponse(memberAddress);
                memberAddressResponseList.add(memberAddressResponse);
            }
            return memberAddressResponseList;
        }
        return null;
    }

    /**
     * 처음 주소 삭제시에는 Status 가 Deleted 상태가 됩니다. ( 현재 배달 중 및 서비스 로직의 문제가 생기지 않게 하기 위해서 )
     * 이 후 스케쥴러로 Status 가 Deleted 상태인 주소들은 DB 에서 삭제합니다.
     * 해당 서비스 로직은 스케쥴러에서 호출합니다.
     */
    @Transactional
    public void deleteAllAddressDeleteStatus() {
        memberAddressRepository.deleteAllByStatus(Status.DELETED);
    }

}
