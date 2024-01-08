package com.example.makedelivery.domain.order.repository;

import com.example.makedelivery.domain.order.domain.entity.Order;
import com.example.makedelivery.domain.order.domain.entity.Order.OrderStatus;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderByIdAndMemberId(Long id, Long memberId);

    Optional<Order> findOrderByIdAndStoreId(Long id, Long storeId);

    Optional<List<Order>> findAllByMemberIdOrderByUpdateDateTimeDesc(Long id);

    Optional<List<Order>> findAllByStoreIdOrderByUpdateDateTimeDesc(Long storeId);
}
