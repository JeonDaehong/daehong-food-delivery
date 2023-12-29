package com.example.makedelivery.common.scheduler;

import com.example.makedelivery.domain.member.service.MemberAddressService;
import com.example.makedelivery.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAddressScheduler {

    private final MemberAddressService memberAddressService;

    /**
     * Status 가 "삭제" 상태인 주소들을 DB 에서 실제로 삭제합니다.
     * <br><br>
     * 회원정보나, 매장정보등은 스케쥴러가 아니라 운영자들이 직접 확인하고 삭제하게끔 설계하였습니다.
     * 반면 주소정보는 회원, 매장 정보에 비해 중요도가 낮고, 빠르게 데이터가 삭제되어도
     * 문제가 되지 않을 요소이기 때문에, 스케쥴러를 통하여 관리하도록 설계하였습니다.
     */
    @Scheduled(fixedDelay = 1000 * 60 * 10) // 10분에 한 번 실행
    public void deleteAllAddressDeleteStatus() {
        memberAddressService.deleteAllAddressDeleteStatus();
    }

}
