package com.smore.product.infrastructure.search.repository;

import com.smore.product.infrastructure.search.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchRepository
        extends ElasticsearchRepository<ProductDocument, String> {

    // 키워드 검색 (name + description)
    List<ProductDocument> findByNameContainingOrDescriptionContaining(
            String name,
            String description
    );

    // 카테고리 + 상태 필터
    List<ProductDocument> findByCategoryIdAndStatus(
            String categoryId,
            String status
    );
}