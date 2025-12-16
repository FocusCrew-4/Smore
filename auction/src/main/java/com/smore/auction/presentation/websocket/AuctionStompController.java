package com.smore.auction.presentation.websocket;

import com.smore.auction.application.usecase.AuctionBidCalculator;
import com.smore.auction.presentation.websocket.dto.request.AuctionBidRequestDto;
import com.smore.auction.presentation.websocket.dto.response.AuctionBidResponseDto;
import com.smore.auction.presentation.websocket.mapper.AuctionWebSocketMapper;
import java.security.Principal;
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
    private final AuctionWebSocketMapper mapper;
    private final AuctionBidCalculator auctionBidCalculator;

    // TODO: massage 받는 곳에선 입력값 검증등만 하고 아무것도 하면 안 됨 고도화때 리팩터링 필수
    @MessageMapping("/auction/{auctionId}/bid")
    public void handleBid(
        @DestinationVariable UUID auctionId,
        Principal principal,
        AuctionBidRequestDto auctionBidRequestDto
    ) {
        log.info("Received a bid request for {}", auctionId);
        log.info("Received a bid request for {}", auctionBidRequestDto);
        AuctionBidResponseDto res
            = mapper.toAuctionBidResponseDto(
                auctionBidCalculator.calculateBid(auctionBidRequestDto.bidPrice(), auctionBidRequestDto.quantity(),
                    String.valueOf(auctionId), principal.getName())
            );
        simpMessagingTemplate.convertAndSend(
            "/topic/auction/" + auctionId,
            "1위 입찰가" + res.highestBid() + ", 10위 입찰가"
                + res.minQualifyingBid()
        );
    }
}
