package com.smore.auction.infrastructure.persistance.jpa.mapper;

import com.smore.auction.domain.model.AuctionBidderRank;
import com.smore.auction.domain.vo.Bidder;
import com.smore.auction.infrastructure.persistance.jpa.entity.AuctionBidderRankJpa;
import com.smore.auction.infrastructure.persistance.jpa.vo.BidderEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuctionBidderRankJpaMapper {

    AuctionBidderRankJpa toEntity(AuctionBidderRank auctionBidderRank);

    AuctionBidderRank toDomain(AuctionBidderRankJpa bidderRankJpa);

    BidderEmbeddable toEntity(Bidder bidder);

    Bidder toDomain(BidderEmbeddable bidderEmbeddable);
}
