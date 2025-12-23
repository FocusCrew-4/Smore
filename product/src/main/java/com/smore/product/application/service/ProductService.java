package com.smore.product.application.service;

import com.smore.product.domain.entity.ProductStatus;
import com.smore.product.domain.event.AuctionPendingStartEvent;
import com.smore.product.domain.event.AuctionStartedEvent;
import com.smore.product.domain.event.LimitedSalePendingStartEvent;
import com.smore.product.domain.event.publisher.ProductEventPublisher;
import com.smore.product.domain.sale.dto.ProductSaleResponse;
import com.smore.product.domain.sale.repository.ProductSaleRepository;
import com.smore.product.domain.stock.dto.StockLogResponse;
import com.smore.product.domain.stock.repository.StockLogRepository;
import com.smore.product.infrastructure.consumer.dto.BidLimitedSaleFinishedEvent;
import com.smore.product.infrastructure.search.document.ProductDocument;
import com.smore.product.infrastructure.search.repository.ProductSearchRepository;
import com.smore.product.presentation.dto.request.CreateProductRequest;
import com.smore.product.presentation.dto.request.UpdateProductRequest;
import com.smore.product.presentation.dto.request.UpdateProductStatusRequest;
import com.smore.product.presentation.dto.response.ProductResponse;
import com.smore.product.domain.entity.Product;
import com.smore.product.domain.entity.SaleType;
import com.smore.product.domain.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ProductSaleRepository productSaleRepository;
    private final StockLogRepository stockLogRepository;
    private final ProductEventPublisher eventPublisher;
    private final EntityManager em;

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
                req.getThresholdForAuction(),
                req.getStatus(),
                LocalDateTime.parse(req.getStartAt()),
                LocalDateTime.parse(req.getEndAt())

        );

        Product saved = productRepository.save(product);

        log.info("product startAt : {}, endAt : {}", product.getStartAt(), product.getEndAt());
        log.info("saved startAt : {}, endAt : {}", saved.getStartAt(), saved.getEndAt());

        LimitedSalePendingStartEvent event =
            LimitedSalePendingStartEvent.builder()
                .productId(saved.getId())
                .categoryId(saved.getCategoryId())
                .sellerId(saved.getSellerId())
                .productPrice(saved.getPrice())
                .stock(saved.getStock())
                .startAt(saved.getStartAt())
                .endAt(saved.getEndAt())
                .idempotencyKey(UUID.randomUUID())
                .build();

        eventPublisher.publishLimitedSalePendingStart(event);

        // ES 색인
        productSearchRepository.save(ProductDocument.from(saved));

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

        // ES 재색인
        productSearchRepository.save(
                ProductDocument.from(product)
        );

        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProductStatus(UUID productId, UpdateProductStatusRequest req) {

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        ProductStatus oldStatus = product.getStatus();
        product.changeStatus(req.getStatus());

        // ES 재색인
        productSearchRepository.save(
                ProductDocument.from(product)
        );

        //상태가 ON_SALE로 바뀌는 순간 이벤트 발행
        if (oldStatus != ProductStatus.ON_SALE && req.getStatus() == ProductStatus.ON_SALE) {

            switch (product.getSaleType()) {

                case AUCTION -> {
                    eventPublisher.publishAuctionPendingStart(
                            AuctionPendingStartEvent.builder()
                                    .sellerId(product.getSellerId())
                                    .productId(product.getId())
                                    .categoryId(product.getCategoryId())
                                    .productPrice(product.getPrice())
                                    .stock(product.getStock())
                                    .idempotencyKey(UUID.randomUUID())
                                    .createdAt(LocalDateTime.now())
                                    .build()
                    );
                }

                case NORMAL, LIMITED_TO_AUCTION -> {
                    eventPublisher.publishLimitedSalePendingStart(
                            LimitedSalePendingStartEvent.builder()
                                    .productId(product.getId())
                                    .categoryId(product.getCategoryId())
                                    .sellerId(product.getSellerId())
                                    .productPrice(product.getPrice())
                                    .stock(product.getStock())
                                    .startAt(product.getStartAt())
                                    .endAt(product.getEndAt())
                                    .idempotencyKey(UUID.randomUUID())
                                    .build()
                    );
                }
            }
        }

        return new ProductResponse(product);
    }

    @Transactional
    public void deleteProduct(UUID productId, Long requesterId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 이미 삭제된 상품 처리
        if (product.getDeletedAt() != null) {
            throw new IllegalStateException("이미 삭제된 상품입니다.");
        }

        product.softDelete(requesterId);

        // ES 문서 제거
        productSearchRepository.deleteById(productId.toString());
    }

    public List<ProductSaleResponse> getProductSales(UUID productId) {

        // 상품 존재 여부 체크
        productSaleRepository.findByProductId(productId);
//                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return productSaleRepository.findByProductId(productId).stream()
                .map(ProductSaleResponse::new)
                .toList();
    }

    public List<StockLogResponse> getStockLogs(UUID productId) {

        productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        return stockLogRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(StockLogResponse::new)
                .toList();
    }

    public void applyFinishedSale(BidLimitedSaleFinishedEvent event) {
        Product product = productRepository.findById(event.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        product.decreaseStock(event.getSoldQuantity());

        productRepository.save(product);
        // 여기서 StockLog 남기고 싶으면 나중에 추가
    }

    @Transactional
    public void startAuction(UUID productId) {

        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        eventPublisher.publishAuctionStarted(
                AuctionStartedEvent.builder()
                        .productId(product.getId())
                        .biddingDuration(product.getBiddingDuration())
                        .idempotencyKey(UUID.randomUUID())
                        .createdAt(LocalDateTime.now())
                        .build()
        );
    }
}
