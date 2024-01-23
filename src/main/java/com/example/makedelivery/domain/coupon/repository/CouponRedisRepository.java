package com.example.makedelivery.domain.coupon.repository;

import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;

import static com.example.makedelivery.common.constants.RedisKeyConstants.*;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {

    private final HashOperations<String, Object, Long> hashOperations;

    public void deletedCouponRedisKeys() {
        hashOperations.delete(COUPON_COUNT);
        hashOperations.delete(COUPON_APPLIED_MEMBER);
    }

    public Long incrementCouponCount(Coupon.CouponType couponType) {
        return hashOperations
                .increment(COUPON_COUNT, couponType.toString(), 1);

    }

    public long addMemberCoupon(Long memberId, Coupon.CouponType couponType) {
        if (hashOperations.get(COUPON_APPLIED_MEMBER, couponType) == null) {
            hashOperations.put(COUPON_APPLIED_MEMBER, couponType, memberId);
            return 1L;
        }
        return 0L; // 이미 중복된 값이 있음
    }

}
