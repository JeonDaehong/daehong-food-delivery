package com.example.makedelivery.domain.coupon.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.common.utils.CouponUseServiceFactory;
import com.example.makedelivery.domain.coupon.domain.MyCouponListResponse;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon.Capacity;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon.CouponType;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon.Status;
import com.example.makedelivery.domain.coupon.domain.entity.ExceptionCoupon;
import com.example.makedelivery.domain.coupon.repository.CouponRedisRepository;
import com.example.makedelivery.domain.coupon.repository.CouponRepository;
import com.example.makedelivery.domain.coupon.repository.ExceptionCouponRepository;
import com.example.makedelivery.domain.member.model.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final ExceptionCouponRepository exceptionCouponRepository;
    private final CouponRedisRepository couponRedisRepository;

    private final CouponUseServiceFactory couponUseServiceFactory;

    /**
     * Redis 에는 incr 명령어가 있습니다.
     * key 에 대한 value 를 1씩 증가시키고, 그걸 return 하는 건데,
     * 이게 싱글스레드로 동작하기 때문에 Lock 과 비슷하지만 더 좋은 성능을 낼 수 있습니다.
     * 특히, 메서드 안의 여러 로직에 전부 Lock 을 거는게 아니라,
     * 발급한 쿠폰 갯수 자체에만 싱글스레드로 접근하는 거기 때문에, 더 좋은 성능을 낼 수 있습니다.
     * 즉, count 를 세는 구간에서 앞에 먼저 들어간 Thread 를 뒤의 Thread 들이 기다리며, 최신 값들을 받아가기 때문에
     * 레이스 컨디션에 걸리지 않게 됩니다.
     * 단, 이 방법은 발급 가능한 쿠폰의 갯수가 많아지면, DB에 과부하를 주게 됩니다.
     * 왜냐하면 레디스로 체크이후 바로 RDB 에 저장하는 로직이 다이렉으로 이어지기 때문입니다.
     * 해당 RDB 가 쿠폰 전영 RDB 가 아니라, 다른 데이터도 함께 들어있고, 여러 서비스에서 접근을 한다면 더더욱 문제가 생길 수 있습니다.
     * 그 이유는,
     * 1. 예를 들어, 현재 사용하는 MySQL 의 성능이 1분에 100개의 insert 가 가능하다고 가정하였을 때
     * 10시에 쿠폰 10,000 개 발급 요청이 이루어지고, 10시 1분에 주문 로직
     * 10시 2분에 회원가입 로직이 발생한다고 가정하였을 때
     * 쿠폰 발급에만 100분이 걸려서, 주문이나 회원가입은 계속 기다려야 하며
     * 주문이나 회원가입 뿐 아니라, 몇몇 쿠폰 발급까지도 안되는 문제가 생길 수 있습니다.
     * 2. 또 다른 문제로는 짧은 시간 내에 DB 에 많은 요청이 들어온다면
     * 순간적으로 DB 리소스를 많이 사용하게 되고, 그게 과부하가 되어
     * 서비스 지연 혹은 오류로 이어질 수 있습니다.
     * nGrinder 를 활용한다면 순간 트래픽이 몰리면 어떻게 되는지 테스트를 해볼 수 있습니다.
     * 이 문제를 해결하기 위해서는 Kafka 를 활용 할 수 있다고 합니다.
     * Kafka 는 비동기 방식으로 동작합니다.
     * 즉, 프로듀서(Producer)가 데이터를 생성하면 즉시 데이터베이스에 삽입하지 않고 Kafka 토픽으로 전송하며,
     * 이는 프로듀서와 컨슈머(Consumer) 간에 시간적 분리를 제공하므로 프로듀서와 데이터베이스 간의 독립성이 확보되므로,
     * 짧으 시간안에 많은 트래픽이 들어와 바로 DB 와 연결하는 방법에 비해 과부하를 많이 줄일 수 있다고 합니다.
     * 단, Kafka 에 대한 이해와 공부가 부족하여, 이러한 방법이 있다는 것만 이해하고 그 부분은 구현하지 않았습니다.
     */
    @Transactional
    public void applyCoupon(Long memberId, CouponType couponType, Capacity capacity) {

        if ( couponRedisRepository.addMemberCoupon(memberId, couponType) != 1 ) {
            throw new ApiException(COUPON_OVERLAB_ERROR); // 한 유저가 중복해서 받는 것을 방지
        }
        if ( couponRedisRepository.incrementCouponCount(couponType) > capacity.getDescription() ) {
            throw new ApiException(COUPON_END);
        }

        try {

            Coupon coupon = Coupon.builder()
                    .memberId(memberId)
                    .couponType(couponType)
                    .status(Status.DEFAULT)
                    .expireDateTime(LocalDateTime.now().plusYears(1)) // 쿠폰은 발급 후 1년 안에 사용해야 하는 정책이 있는 거로 설계.
                    .build();

            couponRepository.save(coupon);

        } catch (Exception e) {

            log.error("Failed to create coupon :: " + memberId);

            ExceptionCoupon exceptionCoupon = ExceptionCoupon.builder()
                    .memberId(memberId)
                    .couponType(couponType)
                    .build();

            exceptionCouponRepository.save(exceptionCoupon); // 에러가 발생하면, 백업 테이블에 저장

        }
    }

    /**
     * Exception 쿠폰 테이블에 저장된 데이터를
     * 배치 처리를 통하여 쿠폰 데이터로 옮겨줍니다.
     */
    @Transactional
    public void moveExceptionCouponData() {
        List<ExceptionCoupon> exceptionCouponList = exceptionCouponRepository.findAll();
        LocalDateTime expireDateTime = LocalDateTime.now().plusYears(1); // 쿠폰은 발급 후 1년 안에 사용해야 하는 정책이 있는 거로 설계.

        List<Coupon> coupons = exceptionCouponList.stream()
                .map(exceptionCoupon -> Coupon.builder()
                        .memberId(exceptionCoupon.getMemberId())
                        .couponType(exceptionCoupon.getCouponType())
                        .status(Status.DEFAULT)
                        .expireDateTime(expireDateTime)
                        .build())
                .toList();

        couponRepository.saveAll(coupons);
    }

    /**
     * 배치 처리를 통하여, 기간이 지난 쿠폰들은 Expired 처리를 해줍니다.
     * 다만, 사용 기간은 지났는데 아직 배치 처리 전일 수도 있기 때문에
     * 쿠폰 사용 로직에서는 시간 단위로 철저히 검사를 진행해줍니다.
     */
    @Transactional
    public void changeExpiredCoupon() {
        List<Coupon> coupons = couponRepository.findByExpireDateTimeBeforeAndStatus(LocalDateTime.now(), Status.DEFAULT).orElse(List.of());
        coupons.forEach(Coupon::changeExpiredCoupon);
    }

    @Transactional(readOnly = true)
    public List<MyCouponListResponse> getMyCouponList(Long memberId) {
        return couponRepository.findAllByMemberId(memberId)
                .orElse(List.of())
                .stream()
                .map(MyCouponListResponse::toMyCouponListResponse)
                .toList();
    }

    /**
     * 지금은 쿠폰의 종류가 두 가지지만, 추후 굉장히 여러가지가 될 수 있습니다.
     * 그렇기 때문에 쿠폰의 타입마다 다르게 동작 할 수 있도록
     * CouponUseService 라는 인터페이스를 만들어 분리하였습니다.
     */
    @Transactional
    public void useCoupon(Member member, Long couponId) {

        Coupon coupon = couponRepository.findCouponByIdAndMemberIdOptimisticLock(couponId, member.getId())
                .orElseThrow( () -> new ApiException(COUPON_NOT_FOUND) );

        if ( coupon.getStatus().equals(Status.USED)) throw new ApiException(COUPON_ALREADY_USED); // 이미 사용한 쿠폰입니다.


        // 기간은 만료되었지만 상태 수정에 대한 배치 처리 이전일 수 있기 때문에, 시간으로도 검사를 진행해줍니다.
        if ( coupon.getStatus().equals(Status.EXPIRED) || coupon.getExpireDateTime().isBefore(LocalDateTime.now()) ) {
            throw new ApiException(COUPON_EXPIRED);
        }

        CouponUseService couponUseService = couponUseServiceFactory.getCouponUseService(coupon.getCouponType());
        couponUseService.useCoupon(member, coupon);

    }

    /**
     * 관리자에 의해
     * 쿠폰 관련 데이터가 담긴 레디스의 데이터를 삭제할 수 있습니다.
     */
    @Transactional
    public void deletedCouponRedisKeys() {
        couponRedisRepository.deletedCouponRedisKeys();
    }

}
