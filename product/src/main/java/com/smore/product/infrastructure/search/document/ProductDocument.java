package com.smore.product.infrastructure.search.document;

import com.smore.product.domain.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(indexName = "products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDocument {

    @Id
    private String id;

    private String categoryId;

    private String name;
    private String description;

    private BigDecimal price;

    private String saleType;
    private String status;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startAt;

    @Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endAt;

    public static ProductDocument from(Product product) {
        return ProductDocument.builder()
                .id(product.getId().toString())
                .categoryId(product.getCategoryId().toString())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .saleType(product.getSaleType().name())
                .status(product.getStatus().name())
                .createdAt(product.getCreatedAt())
                .startAt(product.getStartAt())
                .endAt(product.getEndAt())
                .build();
    }
}