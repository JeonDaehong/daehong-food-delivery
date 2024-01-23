package com.example.makedelivery.domain.coupon.repository;

import com.example.makedelivery.domain.coupon.domain.MyCouponListResponse;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon.Status;
import com.example.makedelivery.domain.order.domain.entity.Order;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<List<Coupon>> findAllByMemberId(Long memberId);

    Optional<Coupon> findCouponByIdAndMemberId(Long id, Long memberId);

    Optional<List<Coupon>> findByExpireDateTimeBeforeAndStatus(LocalDateTime currentTime, Status status);

    // 낙관적 락
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select c from Coupon c where c.id = :id and c.memberId = :memberId")
    Optional<Coupon> findCouponByIdAndMemberIdOptimisticLock(Long id, Long memberId);

}
