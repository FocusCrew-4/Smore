package com.smore.seller.domain.repository;

import com.smore.seller.domain.model.Seller;

public interface SellerRepository {
    Seller save(Seller seller);

    Seller findByMemberId(Long id);
}
