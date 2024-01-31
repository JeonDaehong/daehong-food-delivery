package com.example.makedelivery.domain.coupon.repository;

import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.example.makedelivery.common.constants.RedisKeyConstants.*;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void deletedCouponRedisKeys() {
        redisTemplate.delete(COUPON_COUNT);
        redisTemplate.delete(COUPON_APPLIED_MEMBER);
    }

    public Long incrementCouponCount(Coupon.CouponType couponType) {
        return redisTemplate.opsForHash()
                .increment(COUPON_COUNT, couponType.toString(), 1);

    }

}
