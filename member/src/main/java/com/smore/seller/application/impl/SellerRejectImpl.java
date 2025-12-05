package com.smore.seller.application.impl;

import com.smore.seller.application.usecase.SellerReject;
import com.smore.seller.domain.model.Seller;
import com.smore.seller.domain.repository.SellerRepository;
import java.time.Clock;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerRejectImpl implements SellerReject {

    private final SellerRepository repository;
    private final Clock clock;

    @Override
    public void rejectSeller(Long id) {
        Seller seller = repository.findByMemberId(id);
        seller.rejectApply(clock);
        repository.save(seller);
    }

    @Override
    public void rejectSeller(UUID uuid) {
        Seller seller = repository.findById(uuid);
        seller.rejectApply(clock);
        repository.save(seller);
    }
}
