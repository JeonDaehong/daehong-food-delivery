package com.example.makedelivery.domain.order.repository;

import com.example.makedelivery.domain.order.domain.entity.Order;
import com.example.makedelivery.domain.order.domain.entity.Order.OrderStatus;
import jakarta.persistence.LockModeType;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findOrderByIdAndMemberId(Long id, Long memberId);

    Optional<Order> findOrderById(Long id);

    Optional<Order> findOrderByIdAndStoreId(Long id, Long storeId);

    Optional<List<Order>> findAllByMemberIdOrderByUpdateDateTimeDesc(Long id);

    Optional<List<Order>> findAllByStoreIdOrderByUpdateDateTimeDesc(Long storeId);

    Optional<List<Order>> findAllByStoreIdAndOrderStatus(Long storeId, OrderStatus status);

    // 낙관적 락
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select s from Order s where s.id = :id")
    Order findByIdWithOptimisticLock(Long id);

}
