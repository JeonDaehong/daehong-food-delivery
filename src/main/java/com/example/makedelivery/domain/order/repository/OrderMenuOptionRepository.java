package com.example.makedelivery.domain.order.repository;

import com.example.makedelivery.domain.order.domain.entity.OrderMenuOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderMenuOptionRepository extends JpaRepository<OrderMenuOption, Long> {
    Optional<List<OrderMenuOption>> findAllByOrderMenuId(Long orderMenuId);
}
