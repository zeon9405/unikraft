package com.unikraft.domain.product;

import com.unikraft.domain.product.dto.ProductRequest;
import com.unikraft.domain.product.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 생성 API
     * @param request 프론트엔드에서 보낸 상품 생성 정보 (JSON)
     * @return 생성된 상품의 ID와 함께 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<Void> createProduct(@RequestBody ProductRequest request) {

        Long productId = productService.createProduct(
                request.getName(),
                request.getPrice(),
                request.getDescription(),
                request.getImageUrl(),
                request.getCategoryName(),
                request.getStockQuantity()
        );

        URI location = URI.create("/api/products/" + productId);
        return ResponseEntity.created(location).build();
    }

    /**
     * 전체 상품 목록 조회 API
     * @return 상품 리스트 (DTO)
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAllProducts() {
        // 1. 서비스 호출
        //    - 서비스 계층에서 이미 ProductResponse DTO 리스트로 변환해서 반환합니다.
        //    - 컨트롤러는 이를 그대로 클라이언트에게 전달합니다.
        return ResponseEntity.ok(productService.findAllProducts());
    }

    /**
     * 상품 상세 조회 API
     * @param id 상품 ID
     * @return 상품 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProduct(@PathVariable Long id) {
        // 1. 서비스 호출
        //    - 서비스 계층에서 이미 ProductResponse DTO로 변환해서 반환합니다.
        return ResponseEntity.ok(productService.findProduct(id));
    }
}