package com.example.makedelivery.domain.order.repository;

import com.example.makedelivery.domain.order.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
