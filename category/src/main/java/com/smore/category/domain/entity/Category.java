package com.smore.category.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "p_categories")
public class Category {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Category create(String name, String description) {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        Category category = new Category();
        category.name = name;
        category.description = description;
        category.createdAt = now;
        category.updatedAt = now;

        return category;
    }

    public void update(String name, String description) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }

        if (description != null) {
            this.description = description;
        }

        this.updatedAt = LocalDateTime.now(Clock.systemUTC());
    }

    private LocalDateTime deletedAt;
    private Long deletedBy;

    public void softDelete(Long requesterId) {
        this.deletedAt = LocalDateTime.now(Clock.systemUTC());
        this.deletedBy = requesterId;
    }
}