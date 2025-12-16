package com.smore.auction.application.usecase.impl;

import com.smore.auction.application.command.AuctionStartCommand;
import com.smore.auction.application.exception.AppErrorCode;
import com.smore.auction.application.exception.AppException;
import com.smore.auction.application.port.out.AuctionRoomRegistry;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.application.usecase.AuctionStart;
import com.smore.auction.domain.model.Auction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionStartImpl implements AuctionStart {

    private final AuctionSqlRepository auctionRepository;
    private final AuctionRoomRegistry roomRegistry;

    // 경매시작 비즈니스 로직
    // TODO: 경매 시작시 stock(불변) 값을 가진 auction:{auctionId}:open:stock 키 생성해서 값으로 stock 저장
    @Override
    public void start(AuctionStartCommand command){

        Auction auction
        = auctionRepository.findByProductId(command.productId());
        if (auction != null && auction.isOpen()) {
            return;
        }
        if (auction == null || !auction.isReady()) {
            throw new AppException(AppErrorCode.AUCTION_NOT_EXIST);
        }
        auction.start();

        roomRegistry.register(auction.getId(), command.duration());

        auctionRepository.save(auction);
    }
}
