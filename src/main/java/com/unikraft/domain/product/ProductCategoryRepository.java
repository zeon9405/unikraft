package com.unikraft.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {

    // 1. 카테고리 이름으로 조회하는 쿼리 메서드
    //    - "TEA", "DESSERT" 등의 이름으로 카테고리 엔티티를 찾을 때 사용합니다.
    //    - Optional을 반환하여, 해당 이름의 카테고리가 없을 경우를 안전하게 처리합니다.
    Optional<ProductCategory> findByName(String name);
}