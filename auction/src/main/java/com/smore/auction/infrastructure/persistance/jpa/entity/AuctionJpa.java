package com.smore.auction.infrastructure.persistance.jpa.entity;

import com.smore.auction.domain.enums.AuctionStatus;
import com.smore.auction.infrastructure.persistance.jpa.vo.ProductEmbeddable;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_auction")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuctionJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "product_id")),
        @AttributeOverride(name = "price", column = @Column(name = "product_price", precision = 19, scale = 2)),
        @AttributeOverride(name = "categoryId", column = @Column(name = "product_category_id"))
    })
    private ProductEmbeddable product;

    private Long stock;
    private Long sellerId;
    @Enumerated(EnumType.STRING)
    private AuctionStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private Long deletedBy;

}
