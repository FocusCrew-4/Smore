package com.smore.seller.infrastructure.persistence;

import com.smore.seller.domain.model.Seller;
import com.smore.seller.domain.repository.SellerRepository;
import com.smore.seller.infrastructure.persistence.jpa.entity.SellerJpa;
import com.smore.seller.infrastructure.persistence.jpa.mapper.SellerJpaMapper;
import com.smore.seller.infrastructure.persistence.jpa.repository.SellerJpaRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SellerRepositoryImpl implements SellerRepository {

    private final SellerJpaRepository repository;
    private final SellerJpaMapper mapper;

    @Override
    public Seller save(Seller seller) {
        SellerJpa sellerJpa = repository.save(mapper.toEntity(seller));

        return mapper.toDomain(sellerJpa);
    }

    @Override
    public Seller findByMemberId(Long id) {
        SellerJpa sellerJpa = repository.findByMemberId(id)
            .orElse(null);

        return mapper.toDomain(sellerJpa);
    }

    @Override
    public Seller findById(UUID id) {
        Optional<SellerJpa> sellerJpa = repository.findById(id);
        return sellerJpa.map(mapper::toDomain)
            .orElse(null);
    }
}
