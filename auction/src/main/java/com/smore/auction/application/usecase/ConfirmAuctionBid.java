package com.smore.auction.application.usecase;

import com.smore.auction.application.command.BidConfirmCommand;

public interface ConfirmAuctionBid {
    void confirm(BidConfirmCommand command);
}
