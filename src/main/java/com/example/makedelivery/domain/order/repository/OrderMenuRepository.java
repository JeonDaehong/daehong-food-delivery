package com.example.makedelivery.domain.order.repository;

import com.example.makedelivery.domain.order.domain.entity.OrderMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderMenuRepository extends JpaRepository<OrderMenu, Long> {

    Optional<List<OrderMenu>> findAllByOrderId(Long orderId);

}
