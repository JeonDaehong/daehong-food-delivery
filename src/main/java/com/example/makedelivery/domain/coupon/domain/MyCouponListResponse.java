package com.example.makedelivery.domain.coupon.domain;

import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyCouponListResponse {

    private Long couponId;
    private LocalDateTime expireDateTime;
    private String couponType;

    public static MyCouponListResponse toMyCouponListResponse(Coupon coupon) {
        return MyCouponListResponse.builder()
                .couponId(coupon.getId())
                .expireDateTime(coupon.getExpireDateTime())
                .couponType(coupon.getCouponType().toString())
                .build();
    }

}
