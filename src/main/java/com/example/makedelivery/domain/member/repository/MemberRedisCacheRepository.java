package com.example.makedelivery.domain.member.repository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.Charsets;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Keys 대신 Redis Scan 을 사용하는 이유
 * 레디스의 명령어중 keys를 이용하면 모든 키값들을 가져올 수 있지만 이 키값들을 가져오는동안 다른 명령어를 수행하지 못합니다.
 * 따라서 성능에 영향을 줄 수 있어 레디스에서는 scan,hscan을 권장합니다.
 * Redis의 keys 명령어는 시간 복잡도가 O(N)입니다.
 * 레디스는 싱글스레드로 동작하기 때문에 이처럼 어떤 명령어를 O(n)시간 동안 수행하면서 lock이 걸린다면
 * 그시간동안 keys 명령어를 수행하기 위해 멈춰버리기 때문입니다.
 * Redis에서 제공하는 Scan명령어는 Keys처럼 한번에 모든 레디스 키를 읽어오는 것이아니라
 * count 값을 정하여 그 count값만큼 여러번 레디스의 모든 키를 읽어오는 것입니다.
 * (기본 count값은 10입니다)
 * 예를들어 레디스에 읽어야하는 키값이 총 10000개라고 치고 count가 10개이면 1000번에 나눠서 이 키를 읽는 것입니다.
 * 커서가 0이 될대까지 while문을 돌며 키를 나눠서 읽는 것입니다.
 * 따라서 count의 개수를 낮게잡으면 count만큼 키를 읽어오는 시간은 적게걸리고 모든 데이터를 읽어오는데
 * 시간이 오래걸리지만 그 사이사이 시간에 다른 요청들을 레디스에서 처리해줄 수 있을 것입니다.
 * 반대로 count의 개수를 높게 잡으면  count의 개수만큼 읽어오는데 시간이 오래걸리고 모든데이터를 읽는데는 시간이 짧게 걸리지만
 * 그 사이사이에 다른 요청을 받는 횟수가 줄어들어 레디스가 다른 요청을 처리하는데 병목이 생길 수 있습니다.
 * <br><br>
 * 또한 execute 를 사용함으로써 콜백 함수를 실행시킵니다.
 * 그러면 해당 작업을 하나의 같은 Redis Connection 안에서 수행하는 것을 보장합니다.
 *
 */
@Repository
@RequiredArgsConstructor
public class MemberRedisCacheRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 로그아웃을 하면,
     * 저장되었던 해당 Member 의 캐시들을 전부 삭제해줍니다.
     */
//    public void evictCachesByMember(Long memberId) {
//        Set<String> keys = redisTemplate.keys("*member:" + memberId + "*");
//        if (keys != null) {
//            redisTemplate.delete(keys);
//        }
//    }
    public void evictCachesByMember(Long memberId) {

        List<String> keyList = new ArrayList<>();

        redisTemplate.execute((RedisCallback<List<String>>) redisConnection -> {
            ScanOptions scanOptions = ScanOptions.scanOptions().match("*member:" + memberId + "*").count(10).build();
            // Redis 서버에서 모든 키를 스캔합니다.
            Cursor<byte[]> cursor = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection().scan(scanOptions);
            while (cursor.hasNext()) {
                byte[] keyBytes = cursor.next();
                String key = new String(keyBytes, StandardCharsets.UTF_8);
                keyList.add(key);
            }
            cursor.close();
            return keyList;
        });

        for ( String key : keyList ) {
            redisTemplate.delete(key);
        }

    }

}
