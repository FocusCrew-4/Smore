package com.smore.product.infrastructure.search.repository;

import com.smore.product.infrastructure.search.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductSearchRepository
        extends ElasticsearchRepository<ProductDocument, String> {
}