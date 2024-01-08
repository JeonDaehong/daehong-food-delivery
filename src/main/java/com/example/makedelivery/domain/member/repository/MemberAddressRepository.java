package com.example.makedelivery.domain.member.repository;

import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.model.entity.MemberAddress.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {

    Optional<MemberAddress> findMemberAddressesByIdAndMemberIdAndStatus(Long id, Long memberId, Status status);

    Optional<List<MemberAddress>> findAllByMemberIdAndStatusOrderByPriority(Long memberId, Status status);

    @Query("SELECT COALESCE(MAX(e.priority), 0) FROM MemberAddress e " +
            "WHERE e.status = :status AND e.memberId = :memberId")
    int findMaxPriorityByStatusAndMemberId(@Param("status") Status status, @Param("memberId") Long memberId);

    Optional<MemberAddress> findMemberAddressesByIdAndMemberId(Long id, Long memberId);

    Optional<MemberAddress> findTopByStatusAndMemberIdOrderByPriorityAsc(Status status, Long memberId);

    @Modifying
    @Query("DELETE FROM MemberAddress e WHERE e.status = :status AND e.updateDateTime <= :cutOffDateTime")
    void deleteAllByStatusAndUpdateDateTime24Hour(@Param("status") Status status,
                                                  @Param("cutOffDateTime")LocalDateTime cutOffDateTime);

}
