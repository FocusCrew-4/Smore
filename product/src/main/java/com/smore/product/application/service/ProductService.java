package com.smore.product.application.service;

import com.smore.product.presentation.dto.request.CreateProductRequest;
import com.smore.product.presentation.dto.response.ProductResponse;
import com.smore.product.domain.entity.Product;
import com.smore.product.domain.entity.SaleType;
import com.smore.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(CreateProductRequest req) {

        if (req.getSaleType() == SaleType.LIMITED_TO_AUCTION
                && req.getThresholdForAuction() == null) {
            throw new IllegalArgumentException("LIMITED_TO_AUCTION requires thresholdForAuction");
        }

        Product product = Product.create(
                req.getSellerId(),
                req.getCategoryId(),
                req.getName(),
                req.getDescription(),
                req.getPrice(),
                req.getStock(),
                req.getSaleType(),
                req.getThresholdForAuction()
        );

        productRepository.save(product);

        return new ProductResponse(product);
    }

    public ProductResponse getProduct(UUID productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return new ProductResponse(product);
    }
    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponse::new);
    }
}
