package com.example.makedelivery.domain.member.repository;

import com.example.makedelivery.domain.member.model.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Long countByMemberIdAndStoreIdIsNot(Long memberId, Long storeId);

    Optional<List<Cart>> findAllByMemberId(Long memberId);

    Optional<Cart> findCartByMenuIdAndMemberId(Long menuId, Long memberId);

    void deleteByIdAndMemberId(Long id, Long memberId);

    void deleteAllByMemberId(Long memberId);

}
