package com.example.makedelivery.common.utils;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon.CouponType;
import com.example.makedelivery.domain.coupon.service.CouponUseService;
import com.example.makedelivery.domain.coupon.service.FebruaryEventCouponUseService;
import com.example.makedelivery.domain.coupon.service.NewYearCouponUseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.makedelivery.common.exception.ExceptionEnum.COUPON_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class CouponUseServiceFactory {

    private final NewYearCouponUseService newYearCouponUseService;
    private final FebruaryEventCouponUseService februaryEventCouponUseService;

    public CouponUseService getCouponUseService(CouponType couponType) {

        CouponUseService couponUseService;

        // 추후 다른 쿠폰이 추가 될 수 있습니다.
        switch (couponType) {
            case NEW_YEAR_EVENT_POINT_5000 -> couponUseService = newYearCouponUseService;
            case FEBRUARY_EVENT_POINT_3000 -> couponUseService = februaryEventCouponUseService;
            default -> throw new ApiException(COUPON_NOT_FOUND);
        }

        return couponUseService;
    }

}
