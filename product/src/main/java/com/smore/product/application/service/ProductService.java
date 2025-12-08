package com.smore.product.application.service;

import com.smore.product.domain.entity.ProductStatus;
import com.smore.product.presentation.dto.request.CreateProductRequest;
import com.smore.product.presentation.dto.request.UpdateProductRequest;
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

    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest req) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        if (product.getStatus() == ProductStatus.ON_SALE) {

            if (req.getPrice() != null && !req.getPrice().equals(product.getPrice())) {
                throw new IllegalStateException("판매 중인 상품은 가격을 변경할 수 없습니다.");
            }

            if (req.getSaleType() != null && req.getSaleType() != product.getSaleType()) {
                throw new IllegalStateException("판매 중인 상품의 판매 유형은 변경할 수 없습니다.");
            }

            if (req.getThresholdForAuction() != null &&
                    !req.getThresholdForAuction().equals(product.getThresholdForAuction())) {
                throw new IllegalStateException("판매 중인 상품의 경매 전환 기준은 수정할 수 없습니다.");
            }
        }

        if (req.getName() != null) product.changeName(req.getName());
        if (req.getDescription() != null) product.changeDescription(req.getDescription());
        if (req.getPrice() != null) product.changePrice(req.getPrice());
        if (req.getStock() != null) product.changeStock(req.getStock());
        if (req.getCategoryId() != null) product.changeCategory(req.getCategoryId());
        if (req.getSaleType() != null) product.changeSaleType(req.getSaleType());
        if (req.getThresholdForAuction() != null) product.changeThreshold(req.getThresholdForAuction());

        return new ProductResponse(product);
    }
}
