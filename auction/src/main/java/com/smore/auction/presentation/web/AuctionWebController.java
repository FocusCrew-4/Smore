package com.smore.auction.presentation.web;

import com.smore.auction.application.command.BidConfirmCommand;
import com.smore.auction.application.result.AuctionBidderRankResult;
import com.smore.auction.application.usecase.ConfirmAuctionBid;
import com.smore.auction.application.usecase.FindUserBidInAuction;
import com.smore.auction.application.usecase.ReleaseWinner;
import com.smore.common.response.ApiResponse;
import com.smore.common.response.CommonResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final ReleaseWinner releaseWinner;

    @GetMapping("/{auctionId}/me/bid")
    public ResponseEntity<CommonResponse<?>> getMyBidInfo(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") String role,
        @PathVariable String auctionId
    ) {
        if (!role.equals("ADMIN") && !role.equals("CUSTOMER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("C401", "권한이 없습니다"));
        }

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
        if (!role.equals("ADMIN") && !role.equals("CUSTOMER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("C401", "권한이 없습니다"));
        }

        confirmAuctionBid.confirm(new BidConfirmCommand(
            requesterId,
            UUID.fromString(auctionId)
        ));

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{auctionId}/me/bid/reject")
    public ResponseEntity<CommonResponse<?>> rejectBid(
        @RequestHeader("X-User-Id") Long requesterId,
        @RequestHeader("X-User-Role") String role,
        @PathVariable String auctionId
    ) {
        if (!role.equals("ADMIN") && !role.equals("CUSTOMER")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("C401", "권한이 없습니다"));
        }
        releaseWinner.releaseFromAuctionId(UUID.fromString(auctionId), requesterId);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
