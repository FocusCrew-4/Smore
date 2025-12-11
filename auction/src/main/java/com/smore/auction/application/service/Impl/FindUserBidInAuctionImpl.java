package com.smore.auction.application.service.Impl;

import com.smore.auction.application.result.AuctionBidderRankResult;
import com.smore.auction.application.service.usecase.FindUserBidInAuction;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.model.AuctionBidderRank;
import jakarta.ws.rs.core.NoContentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TODO: 예외타입 변경필요
@Service
@RequiredArgsConstructor
public class FindUserBidInAuctionImpl implements FindUserBidInAuction {

    private final AuctionSqlRepository repository;

    @Override
    public AuctionBidderRankResult findUserBidInAuction(Long userId, String auctionId)
        throws NoContentException {
        AuctionBidderRank bidderRank
            = repository.findBidByAuctionIdAndBidderId(auctionId, userId);

        if (bidderRank.getBidder() == null) {
            throw new NoContentException("유저 정보가 없습니다");
        }

        return new AuctionBidderRankResult(
            bidderRank.getBidder().price(),
            bidderRank.getBidder().quantity(),
            bidderRank.getBidder().status(),
            bidderRank.getRank()
        );
    }
}
