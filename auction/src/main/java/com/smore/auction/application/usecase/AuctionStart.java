package com.smore.auction.application.usecase;

import com.smore.auction.application.command.AuctionStartCommand;

public interface AuctionStart {

    void start(AuctionStartCommand command);
}
