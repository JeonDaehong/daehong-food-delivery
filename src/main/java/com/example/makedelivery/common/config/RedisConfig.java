package com.example.makedelivery.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.time.Duration;

/**
 * 1. @Value : Spring이 지원하는 의존성 주입 방법중 하나입니다.
 * application.properties의 속성값을 프로퍼티에 넣어줍니다.
 * <br><br>
 * 2. RedisConnectionFactory을 생성하여 스프링 세션을 레디스 서버로 연결시킵니다.
 * RedisConnectionFactory는 Connection 객체를 생성하여 관리하는 인터페이스입니다.
 * RedisConnection을 리턴합니다. RedisConnection은 Redis 서버와의 통신을 추상화합니다.
 * <br><br>
 * 3. Redis Connection Factory를 리턴해주는 라이브러리로 Lettuce를 선택하였습니다.
 * 비동기로 요청하기 때문에 높은성능을 가지기 때문입니다.
 * Lettuce는 Netty기반이며 Netty는 비동기 네트워크 프레임워크입니다.
 * Netty는 Channel에서 발생하는 이벤트들을 EventLoop로 비동기 처리하는 구조입니다.
 * <br><br>
 * 4. RedisCacheManager: Cache Manager는 스프링에서 추상화한 인터페이스고 이를 레디스 방식으로
 * 구현한 것이 RedisCacheManager입니다. serializeKeysWith, serializeValueWith로
 * 캐시 Key와 Value를 직렬화,역직렬화 할때 설정을 해줍니다.
 * <br><br>
 * 5. Key에는 StringRedisSerializer, Value에는 GenericJackson2JsonRedisSerializer를 사용했는데 이 Serializer는
 * 별도로 Class Type을 지정해줄 필요없이 자동으로 Object를 해당 타입으로 직렬화 해줍니다. ( Key는 String, Value는 JSON )
 * 단점으로는 Object의 Class Type을 레디스에 함께 넣기 때문에
 * 데이터를 꺼내올 때 그 클래스타입으로만 가져올 수 있습니다.
 */
@Configuration
@EnableRedisHttpSession
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    /**
     * 일반적인 레디스 저장소로 사용할 템플릿 메서드
     */
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    /**
     * Redis에 저장된 Spring Session의 객체를 직렬화하고 역직렬화하기 위한 Serializer 설정.
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    /**
     * Redis를 캐시로 사용하기 위한 CacheManager 빈 생성
     * gradle에서 starter-cache 의존성과 @EnableCaching을 통해 RedisCacheManager가 기본 설정으로 만들어지지만,
     * 상세한 동작을 위해 구현하여 사용합니다. RedisCacheConfiguration으로 레디스 캐시 설정을 커스터마이징합니다.
     *
     * 캐시 추상화는 자바 메서드에 적용되어, 캐시에서 사용가능한 정보가 있다면 실행 횟수를 줄입니다.
     * 대상 메서드가 호출될 때마다, 추상화는 메서드가 이미 해당 매개변수로 이미 호출되었는지 확인합니다.
     * 만약에 호출되었다며느 캐시 결과는 실제 메서드 실행 없이 반환합니다.
     * 만약, 메서드가 호출되지 않았다면, 실행이 되고 결과는 캐시에 저장합니다.
     * 이 방법으로 연산이 많은 메서드를 매번 실행시킬 필요 없이 재사용하여 한번만 실행될 수 있도록 합니다.
     */
    @Bean
    public RedisCacheManager redisCacheManager() {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .entryTtl(Duration.ofHours(1L)); // 캐시 유지시간 1시간

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory())
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

}
