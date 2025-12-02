package com.smore.product.application.service;

import com.smore.product.presentation.dto.request.CreateProductRequest;
import com.smore.product.presentation.dto.response.ProductResponse;
import com.smore.product.domain.entity.Product;
import com.smore.product.domain.entity.SaleType;
import com.smore.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse createProduct(CreateProductRequest req) {

        if (req.getSaleType() == SaleType.LIMITED_TO_AUCTION
                && req.getThresholdForAuction() == null) {
            throw new IllegalArgumentException("LIMITED_TO_AUCTION requires thresholdForAuction");
        }

        Product p = Product.builder()
                .sellerId(req.getSellerId())
                .categoryId(req.getCategoryId())
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .stock(req.getStock())
                .saleType(req.getSaleType())
                .thresholdForAuction(req.getThresholdForAuction())
                .build();

        productRepository.save(p);

        return new ProductResponse(p);
    }
}
