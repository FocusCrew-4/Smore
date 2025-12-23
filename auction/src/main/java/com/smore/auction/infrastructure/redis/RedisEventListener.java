package com.smore.auction.infrastructure.redis;

import com.smore.auction.application.usecase.AuctionClose;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

// TODO: 여기서 경매장 TTL 만료이벤트 구독해서 후속 기능 진행하기
// TODO: Redis 알림 유실시 대비로 보조 스케줄러나 DB 만료 시각 기반 쿼리로 현재 시간 지난 OPEN 경매들 찾아 종료하는 세이프 가드 필요(고도화)
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEventListener implements MessageListener {

    private final AuctionClose auctionClose; // 만료 후 처리할 유즈케이스

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String key = new String(message.getBody(), StandardCharsets.UTF_8);

        if (!channel.contains("expired")) return;

        if (key.startsWith("auction:") && key.endsWith(":open")) {
            String auctionId = key.substring("auction:".length(), key.length() - ":open".length());
            try {
                auctionClose.close(auctionId);
            } catch (Exception e) {
                log.error("Auction closing failed for id {}", auctionId, e);
            }
        }
    }

}
