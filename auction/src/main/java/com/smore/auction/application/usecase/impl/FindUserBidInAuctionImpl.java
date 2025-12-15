package com.smore.auction.application.usecase.impl;

import com.smore.auction.application.exception.AppErrorCode;
import com.smore.auction.application.exception.AppException;
import com.smore.auction.application.result.AuctionBidderRankResult;
import com.smore.auction.application.usecase.FindUserBidInAuction;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.model.AuctionBidderRank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// TODO: 예외타입 변경필요
@Service
@RequiredArgsConstructor
public class FindUserBidInAuctionImpl implements FindUserBidInAuction {

    private final AuctionSqlRepository repository;

    @Override
    public AuctionBidderRankResult findUserBidInAuction(Long userId, String auctionId){
        AuctionBidderRank bidderRank
            = repository.findBidByAuctionIdAndBidderId(auctionId, userId);

        if (bidderRank.getBidder() == null) {
            throw new AppException(AppErrorCode.BIDDER_NOT_FOUND);
        }

        return new AuctionBidderRankResult(
            bidderRank.getBidder().price(),
            bidderRank.getBidder().quantity(),
            bidderRank.getStatus(),
            bidderRank.getRank()
        );
    }
}
