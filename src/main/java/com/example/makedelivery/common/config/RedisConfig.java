package com.example.makedelivery.common.config;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.jwt.port}")
    private int redisJwtPort;

    @Value("${spring.data.redis.cache.port}")
    private int redisCachePort;

    @Value("${spring.data.redis.cart.port}")
    private int redisCartPort;

    @Value("${spring.data.redis.rider.port}")
    private int redisDeliveryPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    public RedisConnectionFactory redisJwtConnectionFactory() {

        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisJwtPort);
        redisConfiguration.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisConnectionFactory redisCacheConnectionFactory() {

        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisCachePort);
        redisConfiguration.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisConnectionFactory redisCartConnectionFactory() {

        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisCartPort);
        redisConfiguration.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisConnectionFactory redisDeliveryConnectionFactory() {

        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(redisHost);
        redisConfiguration.setPort(redisDeliveryPort);
        redisConfiguration.setPassword(redisPassword);

        return new LettuceConnectionFactory(redisConfiguration);
    }


    @Bean("redisTemplate") // redisTemplate 라는
    public RedisTemplate<String, Object> deliveryRedisTemplate() {
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer =
                new GenericJackson2JsonRedisSerializer();

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisDeliveryConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(genericJackson2JsonRedisSerializer);

        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager() {

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig() // 기본적인 캐시 설정을 사용
                .disableCachingNullValues() // Null Caching x
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                )
                .entryTtl(Duration.ofDays(1L)); // Cache 설정 기간 : 1일

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisCacheConnectionFactory())
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }




}
