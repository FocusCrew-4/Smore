package com.smore.payment.global.entity;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedBy
    private Long createBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedBy
    private Long updatedBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private UUID deletedBy;

    private LocalDateTime deletedAt;

    public void delete(UUID id) {
        this.deletedBy = id;
        this.deletedAt = LocalDateTime.now();
    }
}
