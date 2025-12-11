package com.smore.auction.infrastructure.redis;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /*
    RedisTemplate<String, String> 의 제네릭은 Redis 자료구조와는 무관하고
    Redis 의 키와 값이 Java 코드에서 어떤 타입으로 직렬화될지를 결정하는 것 뿐
     */
    //region StringRedisTemplate 이 이미 기본으로 제공하고 있고 Redis 에서 트랜잭션이 필요한 상황도 적음
//    @Bean
//    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
//        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(cf);
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new StringRedisSerializer());
//
//        redisTemplate.setEnableTransactionSupport(true);
//
//        return redisTemplate;
//    }
    // endregion

    /**
     * CacheManager 설정
     *
     * @Cacheable: 캐시에 값이 있으면 메서드를 실행하지 않고 캐시 값을 반환한다. 없으면 메서드를 실행하고 결과를 캐시에 저장
     * @CachePut: 메서드를 항상 실행, 실행 결과를 캐시에 강제로 갱신(put)
     * @CacheEvict: 캐시를 삭제한다. 삭제 시점은 메서드 실행 전 또는 후로 조정 가능
     * @Caching: 여러 캐시 어노테이션을 한 메서드에서 조합할 때 사용
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration config =  RedisCacheConfiguration.defaultCacheConfig()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            // 캐시 TTL 기본값 설정
            .entryTtl(Duration.ofMinutes(10));

        // region value = "키" 값에 따른 캐시 시간 설정
//        Map<String, RedisCacheConfiguration> cacheConfig = new HashMap<>();
//
//        cacheConfig.put("product",
//            RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofSeconds(30)));   // product 캐시 TTL 30초
//
//        cacheConfig.put("order",
//            RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(10)));   // order TTL 10분
        // endregion

        return RedisCacheManager.builder(cf)
            .cacheDefaults(config)
            // 캐시를 트랜잭션과 함께 동작하도록
            .transactionAware()
            .build();
    }
}
