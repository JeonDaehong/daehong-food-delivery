package com.example.makedelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/*
    @EnableRedisHttpSession : 기존 서버 세션 저장소를 사용하지 않고
    Redis의 Session Stroage에 Session을 저장하게 해줍니다.
    springSessionRepositoryFilter라는 이름의 필터를 스프링빈으로 생성합니다.
    springSessionRepositoryFilter 필터는 HttpSession을 스프링세션에 의해 지원되는 커스템 구현체로 바꿔줍니다.
    이 어노테이션이 붙은 곳에서는 레디스가 스프링 세션을 지원합니다.

    @EnableCaching: public 메서드에서 캐싱 어노테이션이 존재하는지 모든 스프링 빈을 검사하는 후처리를 제공합니다.
    만약 관련된 어노테이션이 있다면,
    자동적으로 메서드 호출을 인터셉트하는 프록시가 생성되고 캐싱을 처리합니다.

    @EnableScheduling: 이 어노테이션은 Spring 애플리케이션 내에서 스케줄링을 활성화합니다.
    스케줄링은 주기적으로 일정한 주기로 작업을 실행하도록 하는 기능을 활성화합니다.
    이 어노테이션을 사용하면 @Scheduled 어노테이션이 붙은 메서드들이 주기적으로 실행될 수 있습니다.
    
    @EnableJpaAuditing: 이 어노테이션은 생성일, 수정일을 자동으로 관리하기 위한 Auditing 을 활성화 시켜줍니다.
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableJpaAuditing
@EnableRedisHttpSession
public class MakeDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(MakeDeliveryApplication.class, args);
    }

}
