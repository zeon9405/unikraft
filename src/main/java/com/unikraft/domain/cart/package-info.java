/**
 * cart 도메인 패키지
 *
 * 장바구니 기능과 관련된 모든 클래스가 이 패키지에 위치합니다.
 *
 * [패키지 구성]
 * - Cart: 장바구니 엔티티 (회원과 1:1 관계)
 * - CartItem: 장바구니 아이템 엔티티 (장바구니에 담긴 개별 상품)
 * - CartRepository: 장바구니 데이터 접근 인터페이스
 * - CartItemRepository: 장바구니 아이템 데이터 접근 인터페이스
 * - CartService: 장바구니 비즈니스 로직
 * - CartController: 장바구니 REST API
 * - dto: 데이터 전송 객체 (요청/응답 DTO)
 */
package com.unikraft.domain.cart;
