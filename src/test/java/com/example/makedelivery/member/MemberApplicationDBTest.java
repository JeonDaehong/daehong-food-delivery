package com.example.makedelivery.member;

import com.example.makedelivery.common.annotation.LoginCheck.MemberLevel;
import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.common.facade.RedissonLockFacade;
import com.example.makedelivery.domain.member.model.MemberAddressRequest;
import com.example.makedelivery.domain.member.model.MemberAddressResponse;
import com.example.makedelivery.domain.member.model.MemberJoinRequest;
import com.example.makedelivery.domain.member.model.MemberProfileRequest;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.repository.MemberAddressRepository;
import com.example.makedelivery.domain.member.repository.MemberRedisCacheRepository;
import com.example.makedelivery.domain.member.service.MemberAddressService;
import com.example.makedelivery.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.makedelivery.common.exception.ExceptionEnum.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test DB 와 연결하여 테스트를 진행합니다.
 */
@SpringBootTest
public class MemberApplicationDBTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberAddressService memberAddressService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedissonLockFacade redissonLockFacade;

    @Autowired
    private MemberRedisCacheRepository memberRedisCacheRepository;

    @Test
    @DisplayName("회원가입이 정상적으로 진행됩니다.")
    @Transactional
    void testJoin() {
        // given
        MemberJoinRequest joinRequest = MemberJoinRequest.builder()
                .email("aTestAdmin710a@admin.co.kr")
                .password("1a2a3a4a5a!@#")
                .nickname("TestAdmin")
                .memberLevel(MemberLevel.MEMBER)
                .build();
        Member member = MemberJoinRequest.toEntity(joinRequest, passwordEncoder);
        // when
        memberService.join(joinRequest);
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // then
        assertThat(dbMember.getEmail()).isEqualTo(member.getEmail());
    }

    @Test
    @DisplayName("프로필 업데이트가 정상적으로 진행됩니다.")
    @Transactional
    void testUpdateProfile() {
        // given
        MemberProfileRequest profileRequest = MemberProfileRequest.builder().nickname("TestAdminUpdate").build();
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when
        memberService.updateMemberProfile(dbMember, profileRequest);
        // then
        assertThat(dbMember.getNickname()).isEqualTo(profileRequest.getNickname());
    }

    @Test
    @DisplayName("회원 정보가 정상적으로 삭제되고, 다시 재조회 시 Status 가 DELETE 로 출력되야 합니다.")
    @Transactional
    void testDelete() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when
        memberService.deleteMember(dbMember);
        // then
        dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        assertThat(dbMember.getStatus()).isEqualTo(Member.Status.DELETED);
    }

    @Test
    @DisplayName("회원의 메인주소로 지정된 주소를 삭제하면 Exception 을 발생시킵니다.")
    @Transactional
    void deleteMainAddressThrowsException() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            memberAddressService.deleteAddress(dbMember, 1L);
        });
        assertEquals(MAIN_ADDR_DELETE.getCode(), apiException.getError().getCode());
    }


    @Test
    @DisplayName("회원의 포인트 전환이 멀티스레드 환경 속에서도, 동기화가 잘 진행되어 정상적으로 작동되어야 합니다.")
    @Transactional
    void convertPointsToAvailablePointsTestSuccess() throws InterruptedException {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        // when
        for ( int i = 0; i < threadCount; i++ ) {
            executorService.submit(() -> {
               try {
                   redissonLockFacade.convertPointsToAvailablePoints(dbMember, 5_000);
               } finally {
                   latch.countDown();
               }
            });
        }
        latch.await();
        // then
        Member newDbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        System.out.println("Point : " + newDbMember.getPoint());
        System.out.println("Available Point : " + newDbMember.getAvailablePoint());
        assertEquals(0, newDbMember.getPoint());
        assertEquals(5_000, newDbMember.getAvailablePoint());
    }

    @Test
    @DisplayName("Lock 을 사용하지 않았을 경우, 포인트 전환 값이 생각했던 것과 다르게 나와야합니다.")
    @Transactional
    void convertPointsToAvailablePointsTestFail() throws InterruptedException {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        int threadCount = 500;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        // when
        for ( int i = 0; i < threadCount; i++ ) {
            executorService.submit(() -> {
                try {
                    memberService.convertPointsToAvailablePoints(dbMember, 5_000);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        // then
        // 멀티스레드이므로 테스트마다 다른 결과가 나오기 때문에, 맞는 결과가 나올수도 있으므로 assert 로 검사하지 않았습니다.
        Member newDbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        System.out.println("Point : " + newDbMember.getPoint());
        System.out.println("Available Point : " + newDbMember.getAvailablePoint());
    }

    @Test
    @DisplayName("포인트 전환 단위는 최소 5,000원이며, 5,000원 단위로만 전환 가능함. 그러지 않으면 예외 발생.")
    @Transactional
    void convertPointsToAvailablePointsInvalidPointExceptionTest() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            memberService.convertPointsToAvailablePoints(dbMember, 3_000);
        });
        assertEquals(INVALID_POINT_UNIT.getCode(), apiException.getError().getCode());
    }

    @Test
    @DisplayName("전환하려는 포인트보다 보유포인트가 적으면 예외 발생")
    @Transactional
    void convertPointsToAvailablePointsInsufficientExceptionTest() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when & then
        ApiException apiException = assertThrows(ApiException.class, () -> {
            memberService.convertPointsToAvailablePoints(dbMember, 10_000);
        });
        assertEquals(POINTS_INSUFFICIENT.getCode(), apiException.getError().getCode());
    }

    @Test
    @DisplayName("해당 회원에 대한 Redis 캐시 내용이 전부 삭제됩니다.")
    void evictRedisCacheByMemberTest() {
        // given
        Member dbMember = memberService.findMemberByEmail("aTestAdmin710a@admin.co.kr");
        // when
        memberRedisCacheRepository.evictCachesByMember(dbMember.getId());
        // then

    }

}
