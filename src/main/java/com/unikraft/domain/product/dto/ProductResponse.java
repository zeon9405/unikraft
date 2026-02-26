package com.unikraft.domain.product.dto;

import com.unikraft.domain.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private int price;
    private String description;
    private String imageUrl;
    private String categoryName; // 카테고리 이름만 반환
    private int stockQuantity;

    // 1. 엔티티를 DTO로 변환하는 생성자
    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.description = product.getDescription();
        this.imageUrl = product.getImageUrl();
        
        // 2. 카테고리 이름 처리 (Null Safety)
        //    - 카테고리가 없을 경우를 대비하여 null 체크를 합니다.
        if (product.getCategory() != null) {
            this.categoryName = product.getCategory().getName();
        }
        
        this.stockQuantity = product.getStockQuantity();
    }

    // 3. 정적 팩토리 메서드 (선택 사항)
    //    - ProductResponse.of(product) 형태로 깔끔하게 변환할 수 있습니다.
    public static ProductResponse of(Product product) {
        return new ProductResponse(product);
    }
}