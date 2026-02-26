package com.unikraft.domain.order.dto;

import com.unikraft.domain.order.Order;
import com.unikraft.domain.order.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;

    // 1. 주문 상품 목록 (DTO 리스트)
    //    - 주문에는 여러 상품이 포함될 수 있으므로 리스트로 받습니다.
    //    - 엔티티(OrderItem)를 직접 노출하지 않고, DTO(OrderItemResponse)로 변환해서 담아야 합니다.
    private List<OrderItemResponse> orderItems;

    // 2. 엔티티를 DTO로 변환하는 생성자
    public OrderResponse(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();

        // 3. 주문 상품 리스트 변환 로직
        //    - order.getOrderItems()를 스트림으로 변환합니다.
        //    - map(OrderItemResponse::new)를 사용하여 각 주문 상품을 DTO로 변환합니다.
        //    - collect(Collectors.toList())로 리스트로 만듭니다.
        this.orderItems = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
    }

    // 4. 내부 클래스로 OrderItemResponse 정의 (간단하게)
    //    - 주문 상품 정보를 담을 DTO입니다.
    //    - 상품 이름, 주문 가격, 주문 수량 등을 포함합니다.
    @Getter
    @NoArgsConstructor
    public static class OrderItemResponse {
        private String productName;
        private int orderPrice;
        private int count;

        public OrderItemResponse(com.unikraft.domain.order.OrderItem orderItem) {
            this.productName = orderItem.getProduct().getName();
            this.orderPrice = orderItem.getOrderPrice();
            this.count = orderItem.getCount();
        }
    }
}