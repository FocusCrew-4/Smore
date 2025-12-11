package com.smore.payment.payment.infrastructure.persistence.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisKeyExpirationListener implements MessageListener {

    private final FailedPaymentHandler failedPaymentHandler;

    @Override
    public void onMessage(Message message, byte[] pattern) {

        String expiredKey = message.toString();   // 저장했던 key 문자열
        log.info("[REDIS] TTL expired key = {}", expiredKey);

        try {
            failedPaymentHandler.handleExpiredKey(expiredKey);
        } catch (Exception e) {
            log.error("결제 실패 처리 중 오류 expiredKey={}", expiredKey, e);
        }
    }

}
