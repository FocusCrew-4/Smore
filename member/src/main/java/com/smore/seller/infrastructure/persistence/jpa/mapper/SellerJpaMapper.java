package com.smore.seller.infrastructure.persistence.jpa.mapper;

import com.smore.seller.domain.model.Seller;
import com.smore.seller.domain.vo.Money;
import com.smore.seller.infrastructure.persistence.jpa.entity.SellerJpa;
import com.smore.seller.infrastructure.persistence.jpa.vo.MoneyEmbeddable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SellerJpaMapper {

    Seller toDomain(SellerJpa sellerJpa);

    SellerJpa toEntity(Seller seller);

    MoneyEmbeddable toDomain(Money money);

    @Mapping(target = "add", ignore = true)
    @Mapping(target = "minus", ignore = true)
    Money toEntity(MoneyEmbeddable moneyEmbeddable);

}
