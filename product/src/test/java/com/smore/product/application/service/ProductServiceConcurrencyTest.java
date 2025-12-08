package com.smore.product.application.service;

import com.smore.product.domain.entity.Product;
import com.smore.product.domain.entity.ProductStatus;
import com.smore.product.domain.entity.SaleType;
import com.smore.product.domain.repository.ProductRepository;
import com.smore.product.presentation.dto.request.UpdateProductStatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ProductServiceConcurrencyTest {

    @Autowired
    ProductService productService;

    @Autowired
    ProductRepository productRepository;

    @Test
    void concurrent_status_update_test() throws Exception {

        // 1) 테스트용 상품 생성
        Product saved = productRepository.save(
                Product.builder()
                        .sellerId(1L)
                        .categoryId(UUID.randomUUID())
                        .name("테스트 상품")
                        .description("테스트 설명")
                        .price(BigDecimal.valueOf(10000))
                        .stock(10)
                        .saleType(SaleType.NORMAL)
                        .status(ProductStatus.ON_SALE)
                        .build()
        );

        UUID productId = saved.getId();   // 생성된 ID 사용

        // 2) 동시에 상태 변경 실행
        ExecutorService executor = Executors.newFixedThreadPool(10);

        Runnable task1 = () ->
                productService.updateProductStatus(productId,
                        new UpdateProductStatusRequest(ProductStatus.SOLD_OUT));

        Runnable task2 = () ->
                productService.updateProductStatus(productId,
                        new UpdateProductStatusRequest(ProductStatus.INACTIVE));

        for (int i = 0; i < 10; i++) {
            executor.submit(task1);
            executor.submit(task2);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // 3) 최종 결과 확인
        var result = productRepository.findById(productId).orElseThrow();
        System.out.println("최종 상태 = " + result.getStatus());
    }
}
