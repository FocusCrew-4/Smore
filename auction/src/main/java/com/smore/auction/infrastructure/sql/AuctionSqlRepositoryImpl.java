package com.smore.auction.infrastructure.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.enums.BidderStatus;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.domain.model.AuctionBidderRank;
import com.smore.auction.infrastructure.persistance.jpa.AuctionBidderRankJpaRepository;
import com.smore.auction.infrastructure.persistance.jpa.AuctionJpaRepository;
import com.smore.auction.infrastructure.persistance.jpa.entity.AuctionJpa;
import com.smore.auction.infrastructure.persistance.jpa.mapper.AuctionBidderRankJpaMapper;
import com.smore.auction.infrastructure.persistance.jpa.mapper.AuctionJpaMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuctionSqlRepositoryImpl implements AuctionSqlRepository {

    private final AuctionJpaMapper mapper;
    private final AuctionJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;
    private final AuctionBidderRankJpaMapper bidderRankJpaMapper;
    private final AuctionBidderRankJpaRepository bidderRankJpaRepository;

    @SneakyThrows
    @Override
    public Auction save(Auction auction) {

        log.info("Saving auction {}", objectMapper.writeValueAsString(auction));

        AuctionJpa auctionJpa
            = mapper.toEntity(auction);

        log.info("Saving auction {}", objectMapper.writeValueAsString(auctionJpa));

        AuctionJpa savedAuction = jpaRepository.save(auctionJpa);

        log.info("Saved auction {}", objectMapper.writeValueAsString(savedAuction));

        return mapper.toDomain(savedAuction);
    }

    @Override
    public Auction findByProductId(UUID uuid) {
        Optional<AuctionJpa> auction = jpaRepository.findByProductId((uuid));
        return auction.map(mapper::toDomain)
            .orElse(null);
    }

    @Override
    public Auction findById(String auctionId) {
        Auction auction = mapper.toDomain(jpaRepository.findById(UUID.fromString(auctionId))
            .orElse(null));
        return auction;
    }

    @Override
    public void saveAll(List<AuctionBidderRank> ranks) {
        bidderRankJpaRepository.saveAll(ranks.stream()
            .map(bidderRankJpaMapper::toEntity)
            .toList());
    }

    @Override
    public AuctionBidderRank findBidByAuctionIdAndBidderId(String auctionId, Long userId) {
        AuctionBidderRank auctionBidderRank
            = bidderRankJpaRepository.findByAuctionIdAndBidder_Id(UUID.fromString(auctionId), userId)
            .map(bidderRankJpaMapper::toDomain)
            .orElse(null);
        return auctionBidderRank;
    }

    @Override
    public void saveBidder(AuctionBidderRank auctionBidderRank) {
        bidderRankJpaRepository.save(bidderRankJpaMapper.toEntity(auctionBidderRank));
    }

    @Override
    public AuctionBidderRank findWinnerByAuctionIdAndBidder_Id(UUID auctionId,
        Long winnerMemberId) {
        return bidderRankJpaRepository.findByStatusAndAuctionIdAndBidder_Id(BidderStatus.WINNER, auctionId, winnerMemberId)
            .map(bidderRankJpaMapper::toDomain)
            .orElse(null);
    }

    @Override
    public AuctionBidderRank findTop1ByStandByAndAuctionId(UUID auctionId) {
        return bidderRankJpaRepository.findTop1ByStatusAndAuctionIdOrderByRankAsc(BidderStatus.STANDBY, auctionId)
            .map(bidderRankJpaMapper::toDomain)
            .orElse(null);
    }
}
