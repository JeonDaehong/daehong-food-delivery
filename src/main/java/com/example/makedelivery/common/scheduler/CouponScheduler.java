package com.example.makedelivery.common.scheduler;

import com.example.makedelivery.domain.coupon.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponScheduler {

    private final CouponService couponService;

    /**
     * Exception 으로 인해
     * 정상적인 쿠폰 테이블이 아닌, 예외 백업용 테이블에 저장된 데이터를
     * 스케쥴러 배치 처리를 통하여
     * 원래 정상테이블로 옮겨줍니다.
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void moveExceptionCouponData() {
        couponService.moveExceptionCouponData();
    }

    /**
     * 만료 기간이 지난 쿠폰들의 상태를
     * 기간 만료로 바꿔줍니다.
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60)
    public void changeExpiredCoupon() { couponService.changeExpiredCoupon(); }
}
