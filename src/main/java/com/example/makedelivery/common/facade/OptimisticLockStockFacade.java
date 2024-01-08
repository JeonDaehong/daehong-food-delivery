package com.example.makedelivery.common.facade;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.rider.service.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 라이더가 주문을 잡는 경우 낙관적 락을 이용하여 잡을 수 있도록 구현하였습니다.
 * 그 이유는, 동시에 같은 타이밍에 하나의 주문에 두 명 이상의 라이더가
 * 배달 신청을 해버리면, 주문은 하나인데 배차 받은 라이더는 2명이 될 수 있기 때문입니다.
 * 낙관적 락은 JPA 의 @Version 을 통하여 구현할 수 있으며,
 * 어쩌다 한 번씩 충돌하는 것을 방어하기 위한 목적으로 많이 사용합니다.
 * ( 충돌이 빈번하게 일어나는 경우에는 적합하지 않습니다. )
 * 낙관적락의 특징 중 하나는 버전에 따른 재시도 로직을 직접 구현해줘야 한다는 것이고,
 * 재시도 로직 때문에 너무 빈번한 충돌이 발생하는 로직에 사용하면 오히려 성능이 저하될 수 있다는 것을 기억해야 합니다.
 */
@Component
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final RiderService riderService;

    public void registerRider(Member member, Long orderId) throws InterruptedException {
        while (true) {
            try {
                riderService.registerRider(member, orderId);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }

}
