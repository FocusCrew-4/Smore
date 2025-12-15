package com.smore.auction.application.usecase;

import com.smore.auction.application.command.AuctionStartCommand;
import jakarta.ws.rs.core.NoContentException;

public interface AuctionStart {

    void start(AuctionStartCommand command) throws NoContentException;
}
