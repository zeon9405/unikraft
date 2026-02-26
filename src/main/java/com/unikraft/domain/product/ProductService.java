package com.unikraft.domain.product;

import com.unikraft.domain.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public Long createProduct(String name, int price, String description, String imageUrl, String categoryName, int stockQuantity) {
        ProductCategory category = productCategoryRepository.findByName(categoryName)
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 없습니다. name=" + categoryName));

        Product product = Product.builder()
                .name(name)
                .price(price)
                .description(description)
                .imageUrl(imageUrl)
                .category(category)
                .stockQuantity(stockQuantity)
                .build();

        productRepository.save(product);

        return product.getId();
    }

    /**
     * 전체 상품 목록 조회
     * @return 상품 리스트 (DTO)
     */
    public List<ProductResponse> findAllProducts() {
        // 1. 엔티티 리스트 조회
        List<Product> products = productRepository.findAll();

        // 2. DTO 리스트로 변환
        //    - stream().map()을 사용하여 각 엔티티를 ProductResponse DTO로 변환합니다.
        //    - collect(Collectors.toList())로 다시 리스트로 만듭니다.
        return products.stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 상품 상세 조회
     * @param id 상품 ID
     * @return 상품 DTO
     */
    public ProductResponse findProduct(Long id) {
        // 1. 엔티티 조회
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다. id=" + id));

        // 2. DTO로 변환하여 반환
        return new ProductResponse(product);
    }
}