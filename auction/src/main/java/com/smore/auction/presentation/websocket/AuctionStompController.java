package com.smore.auction.presentation.websocket;

import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuctionStompController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/auction/{auctionId}/bid")
    public void handleBid(
        @DestinationVariable UUID auctionId,
        Map<String, Object> map
    ) {
        log.info("Received a bid request for {}", auctionId);
        log.info("Received a bid request for {}", map.get("text"));
        simpMessagingTemplate.convertAndSend(
            "/sub/auction/" + auctionId,
            map
        );
    }
}
