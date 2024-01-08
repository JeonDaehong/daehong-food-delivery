package com.example.makedelivery.domain.member.repository;

import com.example.makedelivery.domain.member.model.entity.Cart;
import com.example.makedelivery.domain.member.model.entity.CartOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartOptionRepository extends JpaRepository<CartOption, Long> {

    Optional<List<CartOption>> findAllByMemberIdAndCartId(Long memberId, Long cartId);

    Optional<CartOption> findCartOptionByMenuOptionIdAndCartId(Long menuOptionId, Long cartId);

    void deleteAllByCartIdAndMemberId(Long cartId, Long memberId);

    void deleteAllByMemberId(Long memberId);

}
