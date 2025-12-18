package com.smore.auction.application.usecase;

import java.util.UUID;

public interface ReleaseWinner {
    void releaseFromProductId(UUID productId, Long targetMemberId);
    void releaseFromAuctionId(UUID auctionId, Long targetMemberId);
}
