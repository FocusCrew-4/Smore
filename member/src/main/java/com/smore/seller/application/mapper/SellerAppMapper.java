package com.smore.seller.application.mapper;

import com.smore.seller.application.result.SellerResult;
import com.smore.seller.domain.model.Seller;
import org.springframework.stereotype.Component;

@Component
public class SellerAppMapper {


    public SellerResult toSellerResult(Seller seller) {
        return new SellerResult(
            seller.getId(),
            seller.getMemberId(),
            seller.getAccountNum(),
            seller.getStatus(),
            seller.getMoney().amount(),
            seller.getCreatedAt(),
            seller.getUpdatedAt(),
            seller.getDeletedAt(),
            seller.getDeletedBy()
        );
    }
}
