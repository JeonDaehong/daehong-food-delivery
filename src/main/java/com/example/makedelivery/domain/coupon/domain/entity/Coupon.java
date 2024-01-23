package com.example.makedelivery.domain.coupon.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "TB_COUPON")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COUPON_ID")
    private Long id;
    
    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "COUPON_TYPE")
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType;

    @Column(name = "STATUS")
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column(name = "EXPIRE_DATETIME", updatable = false)
    private LocalDateTime expireDateTime;

    @Version
    private Long version;


    @Getter
    @RequiredArgsConstructor
    public enum Status {
        DEFAULT("정상"),
        EXPIRED("기간 만료"),
        USED("사용 완료");

        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum CouponType {
        NEW_YEAR_EVENT_POINT_5000("2024년 신년 맞이 5,000 포인트 교환 쿠폰"),
        FEBRUARY_EVENT_POINT_3000("2월 깜짝 이벤트 3,000 포인트 교환 쿠폰");

        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Capacity {
        NEW_YEAR_EVENT_POINT_5000(100), // 해당 쿠폰은 100 개 발급
        FEBRUARY_EVENT_POINT_3000(500); // 해당 쿠폰은 500 개 발급

        private final Integer description;
    }

    @Builder
    public Coupon(Long memberId, CouponType couponType, Status status, LocalDateTime expireDateTime) {
        this.memberId = memberId;
        this.couponType = couponType;
        this.status = status;
        this.expireDateTime = expireDateTime;
    }

    public void changeExpiredCoupon() {
        this.status = Status.EXPIRED;
    }

    public void useCoupon() {
        this.status = Status.USED;
    }

}
