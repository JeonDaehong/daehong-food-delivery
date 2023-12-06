package com.example.makedelivery.domain.member.repository;

import com.example.makedelivery.domain.member.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findMemberByEmail(final String email);

    Optional<Member> findMemberById(final Long id);

    boolean existsByEmail(final String email);

}
