package com.smore.auction.infrastructure.sql;

import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.infrastructure.persistance.jpa.AuctionJpaRepository;
import com.smore.auction.infrastructure.persistance.jpa.entity.AuctionJpa;
import com.smore.auction.infrastructure.persistance.jpa.mapper.AuctionJpaMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuctionSqlRepositoryImpl implements AuctionSqlRepository {

    private final AuctionJpaMapper mapper;
    private final AuctionJpaRepository jpaRepository;

    @Override
    public Auction save(Auction auction) {
        AuctionJpa auctionJpa
            = mapper.toEntity(auction);

        AuctionJpa savedAuction = jpaRepository.save(auctionJpa);

        return mapper.toDomain(savedAuction);
    }

    @Override
    public Auction findByProductId(UUID uuid) {
        Optional<AuctionJpa> auction = jpaRepository.findByProductId((uuid));
        return auction.map(mapper::toDomain)
            .orElse(null);
    }
}
