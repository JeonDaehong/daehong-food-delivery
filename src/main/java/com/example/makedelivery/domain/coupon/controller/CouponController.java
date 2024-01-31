package com.example.makedelivery.domain.coupon.controller;

import com.example.makedelivery.common.annotation.CurrentMember;
import com.example.makedelivery.common.annotation.LoginCheck;
import com.example.makedelivery.common.facade.OptimisticLockFacade;
import com.example.makedelivery.common.facade.RedissonLockFacade;
import com.example.makedelivery.domain.coupon.domain.MyCouponListResponse;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import com.example.makedelivery.domain.coupon.service.CouponService;
import com.example.makedelivery.domain.member.model.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.makedelivery.common.annotation.LoginCheck.*;
import static com.example.makedelivery.common.constants.URIConstants.COUPON_API_URI;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(COUPON_API_URI)
public class CouponController {

    private final CouponService couponService;

    private final RedissonLockFacade redissonLockFacade; // 쿠폰 사용을 위한 Lock

    @PostMapping
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> applyCoupon(@CurrentMember Member member,
                                                  @RequestParam Coupon.CouponType couponType,
                                                  @RequestParam Coupon.Capacity capacity) {
        couponService.applyCoupon(member.getId(), couponType, capacity);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 포인트 전환 로직과 마찬가지로 악의적인 프로그램을 통해
     * 여러번 누름으로써 쿠폰 사용이 여러번 될 수 있으므로
     * Redisson Lock 을 적용하였습니다.
     */
    @PostMapping("/useCoupon")
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<HttpStatus> useCoupon(@CurrentMember Member member,
                                                @RequestParam Long couponId) {
        redissonLockFacade.useCoupon(member, couponId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    @LoginCheck(memberLevel = MemberLevel.MEMBER)
    public ResponseEntity<List<MyCouponListResponse>> getMyCouponList(@CurrentMember Member member) {
        return ResponseEntity.status(HttpStatus.OK).body(couponService.getMyCouponList(member.getId()));
    }

}
