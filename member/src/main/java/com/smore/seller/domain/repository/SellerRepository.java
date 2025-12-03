package com.smore.seller.domain.repository;

import com.smore.seller.domain.model.Seller;
import java.util.UUID;

public interface SellerRepository {
    Seller save(Seller seller);

    Seller findByMemberId(Long id);

    Seller findById(UUID id);
}
