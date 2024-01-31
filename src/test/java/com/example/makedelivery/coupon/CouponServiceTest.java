package com.example.makedelivery.coupon;

import com.example.makedelivery.common.facade.OptimisticLockFacade;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import com.example.makedelivery.domain.coupon.repository.CouponRedisRepository;
import com.example.makedelivery.domain.coupon.repository.CouponRepository;
import com.example.makedelivery.domain.coupon.repository.ExceptionCouponRepository;
import com.example.makedelivery.domain.coupon.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class CouponServiceTest {

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("Redis 로 count 에 Lock 을 건 상태에서 여러명이 한 번에 응모를 하였을 경우에는 정상적으로 동작해야 합니다.")
    @Transactional
    public void applyCouponMultiThreadTestRedis() throws InterruptedException {

        couponService.deletedCouponRedisKeys(); // 이 전 테스트에서 저장된 Redis value 를 전부 삭제

        final int THREAD_COUNT = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 병렬 작업을 간단하게 할 수 있게 해주는 자바의 API
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT); // 모든 스레드 작업이 끝날때까지 기다려주는 코드

        for ( int i=0; i<THREAD_COUNT; i++ ) {
            long memberId = i;
            executorService.submit(() -> {
                try {
                    couponService.applyCoupon(memberId, Coupon.CouponType.NEW_YEAR_EVENT_POINT_5000, Coupon.Capacity.NEW_YEAR_EVENT_POINT_5000);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        long count = couponRepository.count();

        assertThat(count).isEqualTo(100);

    }

}
