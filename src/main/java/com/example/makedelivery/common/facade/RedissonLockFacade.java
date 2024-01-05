package com.example.makedelivery.common.facade;

import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redisson 은 pubsub 기반이기 때문에 레디스의 과부하를 줄일 수 있습니다.
 * 대신 구현에 별도 로직이 들어간다는 것과, Redisson 이라는 라이브러리를 사용해야하는 부담이 있습니다.
 * <br><br>
 * 포인트 전환에 Lock 을 거는 이유는 악의적인 프로그램을 통해
 * 한 번에 여러 스레드로 포인트 전환을 시도하면,
 * 가지고 있는 포인트가 적어도, 더 많은 사용가능 금액으로 전환할 수 있기 때문입니다.
 * 이런 악용을 방지하기 위해 Lock 을 걸어야 합니다.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RedissonLockFacade {

    private final RedissonClient redissonClient;
    private final MemberService memberService;

    public void convertPointsToAvailablePoints(Member member, int desiredChangePoints) {

        RLock lock = redissonClient.getLock(member.getId().toString());

        try {
            // 락 획득 대기 시간 : 10초
            // 실패 시 다음 락 시도까지 대기 시간 : 1초
            boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                log.info("회원 고유 ID : " + member.getId() + " / 포인트 전환 Lock 획득 실패.");
                return;
            }

            memberService.convertPointsToAvailablePoints(member, desiredChangePoints);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
