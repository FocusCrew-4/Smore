package com.smore.auction.presentation.websocket.mapper;

import com.smore.auction.application.result.AuctionBidCalculateResult;
import com.smore.auction.presentation.websocket.dto.response.AuctionBidResponseDto;
import org.springframework.stereotype.Component;

@Component
public class AuctionWebSocketMapper {

    public AuctionBidResponseDto toAuctionBidResponseDto(AuctionBidCalculateResult result) {
        return new AuctionBidResponseDto(
            result.highestBid(),
            result.minQualifyingBid()
        );
    }

}
