package com.smore.auction.infrastructure.persistance.jpa.mapper;

import com.smore.auction.domain.model.Auction;
import com.smore.auction.domain.vo.Product;
import com.smore.auction.infrastructure.persistance.jpa.entity.AuctionJpa;
import com.smore.auction.infrastructure.persistance.jpa.vo.ProductEmbeddable;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuctionJpaMapper {

    Auction toDomain(AuctionJpa auctionJpa);

    AuctionJpa toEntity(Auction auction);

    Product toDomain(ProductEmbeddable productEmbeddable);

    ProductEmbeddable toEntity(Product product);

}
