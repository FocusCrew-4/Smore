package com.smore.auction.application.usecase;

import com.smore.auction.application.result.AuctionBidderRankResult;
import jakarta.ws.rs.core.NoContentException;

public interface FindUserBidInAuction {
    AuctionBidderRankResult findUserBidInAuction(Long userId, String auctionId);
}
