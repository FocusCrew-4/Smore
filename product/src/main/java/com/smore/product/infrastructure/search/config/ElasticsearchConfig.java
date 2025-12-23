package com.smore.product.infrastructure.search.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(
        basePackages = "com.smore.product.infrastructure.search.repository"
)
public class ElasticsearchConfig {
}