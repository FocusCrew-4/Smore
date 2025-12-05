package com.smore.product.application.service;

import com.smore.product.domain.entity.ProductStatus;
import com.smore.product.domain.repository.ProductRepository;
import com.smore.product.presentation.dto.request.UpdateProductStatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        // 테스트용 상품 ID
        UUID productId = UUID.fromString("여기에-테스트-상품-UUID");

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

        var result = productRepository.findById(productId).orElseThrow();
        System.out.println("최종 상태 = " + result.getStatus());
    }
}
