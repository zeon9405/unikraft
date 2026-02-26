package com.unikraft.domain.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
    private String description;
    private String imageUrl;

    // 1. [수정] JSON 변환 시 프록시 객체 무시 설정
    //    - @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}):
    //      Jackson 라이브러리가 JSON으로 변환할 때, Hibernate가 만든 가짜 객체(프록시)의 내부 필드들을 무시하도록 합니다.
    //      이 설정이 없으면 "No serializer found for class..." 에러가 발생합니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProductCategory category;

    private int stockQuantity;

    @Builder
    public Product(String name, int price, String description, String imageUrl, ProductCategory category, int stockQuantity) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.stockQuantity = stockQuantity;
    }

    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            throw new IllegalArgumentException("재고가 부족합니다. (현재 재고: " + this.stockQuantity + ")");
        }
        this.stockQuantity = restStock;
    }

    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }
}