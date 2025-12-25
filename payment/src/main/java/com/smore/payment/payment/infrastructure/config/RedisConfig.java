package com.smore.payment.payment.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smore.payment.payment.application.port.in.ApprovePaymentResult;
import com.smore.payment.payment.domain.model.TemporaryPayment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, TemporaryPayment> temporaryPaymentRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, TemporaryPayment> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // ObjectMapper 설정 (LocalDateTime 처리)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // JSON 직렬화 설정
        Jackson2JsonRedisSerializer<TemporaryPayment> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, TemporaryPayment.class);

        // Key는 String, Value는 JSON
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, ApprovePaymentResult> approvePaymentResultRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, ApprovePaymentResult> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<ApprovePaymentResult> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, ApprovePaymentResult.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
