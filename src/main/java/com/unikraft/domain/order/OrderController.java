package com.unikraft.domain.order;

import com.unikraft.domain.order.dto.OrderRequest;
import com.unikraft.domain.order.dto.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성 API
     * @param loginId 로그인한 사용자의 ID (토큰에서 추출)
     * @param request 주문 요청 정보 (상품 ID, 수량)
     * @return 생성된 주문의 ID와 함께 201 Created 상태 코드
     */
    @PostMapping
    public ResponseEntity<Void> createOrder(@AuthenticationPrincipal String loginId,
                                            @RequestBody OrderRequest request) {

        Long orderId = orderService.createOrder(
                loginId,
                request.getProductId(),
                request.getCount()
        );

        URI location = URI.create("/api/orders/" + orderId);
        return ResponseEntity.created(location).build();
    }

    /**
     * 내 주문 목록 조회 API
     * @param loginId 로그인한 사용자의 ID (토큰에서 추출)
     * @return 주문 목록 (DTO 리스트)
     */
    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> getMyOrders(@AuthenticationPrincipal String loginId) {
        // 1. 서비스 호출
        //    - 토큰에서 추출한 loginId를 서비스에 전달합니다.
        //    - 서비스는 해당 사용자의 주문 목록을 조회하여 DTO 리스트로 반환합니다.
        List<OrderResponse> myOrders = orderService.findMyOrders(loginId);

        // 2. 응답 생성
        //    - 조회된 주문 목록을 200 OK 상태 코드와 함께 반환합니다.
        return ResponseEntity.ok(myOrders);
    }
}