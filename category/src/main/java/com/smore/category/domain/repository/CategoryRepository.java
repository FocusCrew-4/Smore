package com.smore.category.domain.repository;

import com.smore.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByName(String name);

    //삭제되지 않은 카테고리만 전체 조회
    List<Category> findAllByDeletedAtIsNull();

    //삭제되지 않은 카테고리만 단건 조회
    Optional<Category> findByIdAndDeletedAtIsNull(UUID id);
}