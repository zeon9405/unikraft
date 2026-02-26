package com.unikraft.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRequest {

    private String name;
    private int price;
    private String description;
    private String imageUrl;

    private String categoryName;

    private int stockQuantity;
}