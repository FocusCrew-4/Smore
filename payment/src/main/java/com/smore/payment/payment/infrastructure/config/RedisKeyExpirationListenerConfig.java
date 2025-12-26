package com.smore.payment.payment.infrastructure.config;

import com.smore.payment.payment.infrastructure.redis.RedisKeyExpirationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisKeyExpirationListenerConfig{

    @Bean
    public RedisMessageListenerContainer keyExpirationListenerContainer(
            RedisConnectionFactory connectionFactory,
            RedisKeyExpirationListener listener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // DB 0에서 expired 이벤트 구독
        container.addMessageListener(listener, new PatternTopic("__keyevent@0__:expired"));

        return container;
    }
}
