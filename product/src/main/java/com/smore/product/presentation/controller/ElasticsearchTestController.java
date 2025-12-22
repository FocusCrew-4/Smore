package com.smore.product.presentation.controller;

import com.smore.product.infrastructure.search.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/es-test")
@RequiredArgsConstructor
public class ElasticsearchTestController {

    private final ProductSearchRepository productSearchRepository;

    @GetMapping("/ping")
    public String ping() {
        productSearchRepository.count(); // ES에 실제 요청
        return "ES CONNECTED";
    }
}
