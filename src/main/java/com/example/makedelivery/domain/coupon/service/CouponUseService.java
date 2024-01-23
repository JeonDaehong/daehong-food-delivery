package com.example.makedelivery.domain.coupon.service;

import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon.CouponType;
import com.example.makedelivery.domain.member.model.entity.Member;

public interface CouponUseService {

    void useCoupon(Member member, Coupon coupon);

}
