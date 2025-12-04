package com.smore.product.application.service;

import com.smore.product.presentation.dto.request.CreateProductRequest;
import com.smore.product.presentation.dto.request.UpdateProductRequest;
import com.smore.product.presentation.dto.request.UpdateProductStatusRequest;
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

    public ProductResponse getProduct(UUID productId) {
        Product p = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return new ProductResponse(p);
    }

    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(ProductResponse::new);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, UpdateProductRequest req) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품 없음"));

        if (req.getName() != null) product.changeName(req.getName());
        if (req.getDescription() != null) product.changeDescription(req.getDescription());
        if (req.getPrice() != null) product.changePrice(req.getPrice());
        if (req.getStock() != null) product.changeStock(req.getStock());
        if (req.getCategoryId() != null) product.changeCategory(req.getCategoryId());
        if (req.getSaleType() != null) product.changeSaleType(req.getSaleType());
        if (req.getThresholdForAuction() != null) product.changeThreshold(req.getThresholdForAuction());

        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProductStatus(UUID productId, UpdateProductStatusRequest req) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        if (req.getStatus() == null) {
            throw new IllegalArgumentException("상태 값이 필요합니다.");
        }

        product.changeStatus(req.getStatus());

        return new ProductResponse(product);
    }
}
