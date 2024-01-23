package com.example.makedelivery.domain.coupon.service;

import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import com.example.makedelivery.domain.member.model.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class NewYearCouponUseService implements CouponUseService {
    @Override
    public void useCoupon(Member member, Coupon coupon) {
        coupon.useCoupon();
        member.addPoint(5_000);
    }
}
