package com.smore.auction.application.service.usecase;

import com.smore.auction.application.command.AuctionCreateCommand;
import com.smore.auction.domain.model.Auction;

public interface AuctionCreate {

    Auction create(AuctionCreateCommand command);

}
