package com.unikraft.domain.order;

import com.unikraft.domain.member.Member;
import com.unikraft.domain.member.MemberRepository;
import com.unikraft.domain.order.dto.OrderResponse;
import com.unikraft.domain.product.Product;
import com.unikraft.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /**
     * 주문 생성
     * @param loginId 주문자 ID (토큰에서 추출)
     * @param productId 주문할 상품 ID
     * @param count 주문 수량
     * @return 생성된 주문의 ID
     */
    @Transactional
    public Long createOrder(String loginId, Long productId, int count) {

        // 1. 회원 엔티티 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + loginId));

        // 2. 상품 엔티티 조회
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다. id=" + productId));

        // 3. 재고 감소 로직 호출
        product.removeStock(count);

        // 4. 주문 상품(OrderItem) 만들기
        OrderItem orderItem = OrderItem.createOrderItem(product, product.getPrice(), count);

        // 5. 주문(Order) 만들기
        Order order = Order.createOrder(member, orderItem);

        // 6. 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 내 주문 목록 조회
     * @param loginId 로그인한 사용자의 ID (토큰에서 추출)
     * @return 주문 목록 (DTO 리스트)
     */
    public List<OrderResponse> findMyOrders(String loginId) {
        // 1. 회원 조회
        //    - 토큰에서 추출한 loginId로 회원을 찾습니다.
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 없습니다. id=" + loginId));

        // 2. 주문 목록 조회
        //    - 회원의 ID(PK)를 사용하여 주문 목록을 조회합니다.
        List<Order> orders = orderRepository.findAllByMemberId(member.getId());

        // 3. DTO 변환
        //    - 조회된 주문 엔티티 리스트를 OrderResponse DTO 리스트로 변환합니다.
        return orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());
    }
}