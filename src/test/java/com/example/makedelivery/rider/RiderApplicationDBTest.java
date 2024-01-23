package com.example.makedelivery.rider;

import com.example.makedelivery.common.facade.OptimisticLockFacade;
import com.example.makedelivery.domain.coupon.domain.entity.Coupon;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.CartService;
import com.example.makedelivery.domain.member.service.MemberService;
import com.example.makedelivery.domain.order.domain.OrderMenuRequest;
import com.example.makedelivery.domain.order.service.OrderService;
import com.example.makedelivery.domain.rider.domain.DeliveryHistoryResponse;
import com.example.makedelivery.domain.rider.domain.entity.RiderDeliveryHistory;
import com.example.makedelivery.domain.rider.service.RiderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * 낙관적 락을 걸 때에는,
 * 실제 서비스에만 @Transactional 이 걸려있어야 합니다.
 * Facade 와 그 바깥에는 @Transactional 이 걸려있으면 무한 루프가 돌게 됩니다.
 * 그러한 이유로 테스트 코드에 @Transactional 을 적용하면 안됩니다.
 * <br><br>
 * 무한 루프가 도는 이유는
 * MySQL 을 사용한다면 Isolation Level 이 REPEATABLE READ 가 기본으로 설정되어있기 때문에
 * 처음 SELECT 한 값은 트랜잭션이 끝나기 전까지 몇 번을 다시 SELECT 해도 동일한 값으로 읽게 됩니다.
 * 그래서 첫 Version 이 1이라고 가정하였을 때
 * 모든 스레드들이 계속해서 Version 을 1로만 읽으므로,
 * 첫 트랜잭션을 제외한 모든 트랜잭션은 무한히 실패하게 됩니다.
 * 따라서 한 트랜잭션 안에서 업데이트와 재시도 로직이 진행되지 않도록 @Transactional 을 메소드에서 떼주면 정상 동작하게 됩니다.
 * <br><br>
 * 정말로 격리수준 때문인지를 확인하기 위해
 * DB에 Isolation Level 을 READ COMMITTED 로 바꾸고 테스트를 진행해보았는데,
 * 이 때 @Transactional 이 붙어 있어도 정상 동작 하는 것을 확인할 수 있었습니다.
 */
@SpringBootTest
public class RiderApplicationDBTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private RiderService riderService;

    @Autowired
    private OptimisticLockFacade optimisticLockFacade;

    private Member rider;

    @BeforeEach
    void setMember() {
        rider = memberService.findMemberByEmail("dTestAdmin710a@admin.co.kr"); // Rider Test ID
    }

    @Test
    @DisplayName("하나의 주문에 여러 라이더가 동시에 접근 하였을 때, 한 명의 라이더만 그 주문을 잡을 수 있어야 합니다.")
    // @Transactional
    void registerRiderConcurrencyTest() throws InterruptedException {

        final int THREAD_COUNT = 100; // 동시에 100명의 라이더가 접근
        ExecutorService executorService = Executors.newFixedThreadPool(32); // 병렬 작업을 간단하게 할 수 있게 해주는 자바의 API
        CountDownLatch countDownLatch = new CountDownLatch(THREAD_COUNT); // 모든 스레드 작업이 끝날때까지 기다려주는 코드

        for ( int i=0; i<THREAD_COUNT; i++ ) {
            executorService.submit(() -> {
                try {
                    optimisticLockFacade.registerRider(rider, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        List<DeliveryHistoryResponse> deliveryHistoryResponseList = riderService.getMyDeliveryHistory(rider, RiderDeliveryHistory.DeliveryStatus.DELIVERING);
        int count = deliveryHistoryResponseList.size();

        assertThat(count).isEqualTo(1);
    }

}
