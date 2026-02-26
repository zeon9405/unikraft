package com.unikraft.domain.order;

import com.unikraft.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int orderPrice;
    private int count;

    @Builder
    public OrderItem(Order order, Product product, int orderPrice, int count) {
        this.order = order;
        this.product = product;
        this.orderPrice = orderPrice;
        this.count = count;
    }

    // == 생성 메서드 ==
    public static OrderItem createOrderItem(Product product, int orderPrice, int count) {
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .orderPrice(orderPrice)
                .count(count)
                .build();

        // 여기서 추가 로직 수행
        // 예: product.removeStock(count); // 재고 감소

        return orderItem;
    }

    // == 연관관계 편의 메서드 ==
    public void setOrder(Order order) {
        this.order = order;
    }
}