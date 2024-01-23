package com.example.makedelivery.domain.coupon.domain.entity;

import com.example.makedelivery.common.constants.DateEntity;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import jakarta.persistence.*;
import lombok.*;

/**
 * 너무 많은 양의 쿠폰을 발급 받거나,
 * 한 번에 많은 트래픽이 몰리게 되면,
 * DB 에서 너무 많은 INSERT 가 한 번에 일어나기 때문에 DB 과부하가 생겨
 * 다른 서비스 로직에서 오류가 생길 수도 있고,
 * 쿠폰 발급에서 오류가 생길 수도 있습니다. ( 병목 현상 등도 일어날 수 있음 )
 * 또한 해당 프로젝트처럼 JPA 를 사용하는 경우 자체적으로 Blocking Call 을 하기 때문에 병목이 생길 수 있습니다.
 * 그러한 오류에 대응하기 위해서는 Kafka ( 데이터 스트리밍, 파이프라인 등을 위해 설계된 분산 이벤트 스트리밍 오픈 소스 ) 를 사용하는 것이 좋다고 하지만,
 * 현재 저는 Kafka 를 완벽히 이해하고 있지 않으므로, 추후 추가 하기로 하였습니다.
 * 물론 그 떄에도 예기치 못한 상황에 대비하여 이렇게 ExceptionCoupon 과 같은 백업 DB 를 만들어두어야 하지만,
 * 우선 위와 같은 이유로 에러 발생시 백업 받는 Table 을 만들었습니다.
 * 이는 배치(스케쥴러)를 통해 쿠폰 DB에 들어갈 예정입니다.
 */
@Entity
@Getter
@ToString
@Table(name = "TB_COUPON_EXCEPTION")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExceptionCoupon extends DateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EXCEPTION_COUPON_ID")
    private Long id;

    @Column(name = "MEMBER_ID")
    private Long memberId;

    @Column(name = "COUPON_TYPE")
    @Enumerated(value = EnumType.STRING)
    private Coupon.CouponType couponType;

    @Builder
    public ExceptionCoupon(Long memberId, Coupon.CouponType couponType) {
        this.memberId = memberId;
        this.couponType = couponType;
    }

}
