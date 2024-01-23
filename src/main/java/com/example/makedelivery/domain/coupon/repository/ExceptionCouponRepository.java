package com.example.makedelivery.domain.coupon.repository;

import com.example.makedelivery.domain.coupon.domain.entity.ExceptionCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExceptionCouponRepository extends JpaRepository<ExceptionCoupon, Long> {
}
