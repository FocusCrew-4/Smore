package com.smore.product.application.service;

import com.smore.product.infrastructure.search.document.ProductDocument;
import com.smore.product.infrastructure.search.repository.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductSearchRepository productSearchRepository;

    public List<ProductDocument> search(String keyword) {
        return productSearchRepository
                .findByNameContainingOrDescriptionContaining(keyword, keyword);
    }
}
