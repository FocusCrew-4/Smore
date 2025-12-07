package com.smore.auction.infrastructure.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smore.auction.application.sql.AuctionSqlRepository;
import com.smore.auction.domain.model.Auction;
import com.smore.auction.infrastructure.persistance.jpa.AuctionJpaRepository;
import com.smore.auction.infrastructure.persistance.jpa.entity.AuctionJpa;
import com.smore.auction.infrastructure.persistance.jpa.mapper.AuctionJpaMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class AuctionSqlRepositoryImpl implements AuctionSqlRepository {

    private final AuctionJpaMapper mapper;
    private final AuctionJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

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
}
