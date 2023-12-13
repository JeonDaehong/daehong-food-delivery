package com.example.makedelivery.domain.member.repository;

import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.model.entity.MemberAddress.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {

    Optional<MemberAddress> findMemberAddressesByIdAndMemberId(Long id, Long memberId);

    Optional<List<MemberAddress>> findAllByMemberIdAndStatus(Long memberId, Status status);

    void deleteAllByStatus(Status status);

}
