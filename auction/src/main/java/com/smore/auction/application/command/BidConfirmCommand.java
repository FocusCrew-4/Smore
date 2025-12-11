package com.smore.auction.application.command;

import java.util.UUID;

public record BidConfirmCommand(
    Long requesterId,
    UUID auctionId
) {

}
