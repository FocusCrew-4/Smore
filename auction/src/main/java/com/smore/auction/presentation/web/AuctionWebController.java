package com.smore.auction.presentation.web;

import com.smore.auction.application.command.BidConfirmCommand;
import com.smore.auction.application.result.AuctionBidderRankResult;
import com.smore.auction.application.usecase.ConfirmAuctionBid;
import com.smore.auction.application.usecase.FindUserBidInAuction;
import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import jakarta.ws.rs.core.NoContentException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auction")
@RequiredArgsConstructor
public class AuctionWebController {

    private final FindUserBidInAuction findUserBidInAuction;
    private final ConfirmAuctionBid confirmAuctionBid;

    @GetMapping("/{auctionId}/me/bid")
    public ResponseEntity<CommonResponse<?>> getMyBidInfo(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") String role,
        @PathVariable String auctionId
    ) throws NoContentException {

        AuctionBidderRankResult result
        = findUserBidInAuction.findUserBidInAuction(
            requesterId,
            auctionId
        );

        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/{auctionId}/me/bid/confirm")
    public ResponseEntity<CommonResponse<?>> confirmBid(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") String role,
        @PathVariable String auctionId
    ) {

        confirmAuctionBid.confirm(new BidConfirmCommand(
            requesterId,
            UUID.fromString(auctionId)
        ));

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
