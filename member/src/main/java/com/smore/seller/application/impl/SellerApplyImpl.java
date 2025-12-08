package com.smore.seller.application.impl;

import com.smore.seller.application.mapper.SellerAppMapper;
import com.smore.seller.application.usecase.SellerApply;
import com.smore.seller.application.command.ApplyCommand;
import com.smore.seller.application.result.SellerResult;
import com.smore.seller.domain.model.Seller;
import com.smore.seller.domain.repository.SellerRepository;
import java.time.Clock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerApplyImpl implements SellerApply {

    private final SellerRepository repository;
    private final Clock clock;
    private final SellerAppMapper mapper;

    @Override
    public SellerResult sellerApply(ApplyCommand applyCommand) {

        if (repository.findByMemberId(applyCommand.requesterId()) != null) {
            throw new IllegalStateException("이미 존재하는 요청자 입니다");
        }

        Seller newSeller = Seller.apply(
            applyCommand.requesterId(),
            applyCommand.accountNum(),
            clock
        );

        Seller savedSeller = repository.save(newSeller);

        return mapper.toSellerResult(savedSeller);
    }
}
