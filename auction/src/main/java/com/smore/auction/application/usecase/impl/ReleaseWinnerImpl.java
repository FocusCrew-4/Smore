package com.smore.auction.application.usecase.impl;

import com.smore.auction.application.exception.AppErrorCode;
import com.smore.auction.application.exception.AppException;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.application.usecase.ReleaseWinner;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.domain.model.AuctionBidderRank;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReleaseWinnerImpl implements ReleaseWinner {
    private final AuctionSqlRepository auctionSqlRepository;

    @Override
    public void releaseFromProductId(UUID productId, Long targetMemberId) {
        Auction findAuction = auctionSqlRepository.findByProductId(productId);
        reselectWinner(findAuction.getId(), targetMemberId);
    }

    @Override
    public void releaseFromAuctionId(UUID auctionId, Long targetMemberId) {
        reselectWinner(auctionId, targetMemberId);
    }

    private void reselectWinner(UUID auctionId, Long targetMemberId) {
        AuctionBidderRank winnerBidder =
            auctionSqlRepository.findWinnerByAuctionIdAndBidder_Id(auctionId, targetMemberId);
        AuctionBidderRank nextWinner =
            auctionSqlRepository.findTop1ByStandByAndAuctionId(auctionId);

        if (nextWinner == null){
            log.warn("다음 낙찰자가 존재하지 않습니다");
            winnerBidder.releaseWinner();
            auctionSqlRepository.saveBidder(winnerBidder);
            return;
        }
        nextWinner.succeedFrom(winnerBidder);

        auctionSqlRepository.saveBidder(winnerBidder);
        auctionSqlRepository.saveBidder(nextWinner);
    }
}
