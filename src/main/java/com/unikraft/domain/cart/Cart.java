package com.unikraft.domain.cart;

import com.unikraft.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Cart 엔티티 (장바구니)
 *
 * [JPA - Java Persistence API]
 * - @Entity: 이 클래스가 데이터베이스 테이블과 매핑되는 JPA 엔티티임을 나타냅니다.
 * - Spring Data JPA가 이 클래스를 보고 자동으로 'cart' 테이블을 생성합니다.
 *
 * [설계 개념]
 * - 한 명의 회원(Member)은 하나의 장바구니(Cart)를 가집니다. (1:1 관계)
 * - 하나의 장바구니(Cart)는 여러 개의 상품(CartItem)을 담을 수 있습니다. (1:N 관계)
 *
 * [Lombok 라이브러리]
 * - @NoArgsConstructor: 파라미터가 없는 기본 생성자를 자동으로 만들어줍니다.
 *   JPA는 엔티티를 생성할 때 기본 생성자가 필수입니다.
 * - @Getter: 모든 필드의 getter 메서드를 자동으로 생성합니다.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자 (외부에서 new Cart() 호출 방지)
@Getter
public class Cart {

    /**
     * [Primary Key 설정]
     * @Id: 이 필드가 테이블의 Primary Key임을 나타냅니다.
     * @GeneratedValue: Primary Key 값을 자동으로 생성합니다.
     * - strategy = GenerationType.IDENTITY: 데이터베이스의 AUTO_INCREMENT를 사용합니다.
     *   MySQL의 경우 INSERT 시 자동으로 1, 2, 3... 증가하는 ID를 부여합니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * [일대일(1:1) 관계 매핑]
     * @OneToOne: Cart와 Member는 1:1 관계입니다.
     *
     * @JoinColumn: 외래 키(Foreign Key)를 설정합니다.
     * - name = "member_id": 데이터베이스에 생성될 컬럼명
     * - 이 컬럼에 Member 테이블의 id 값이 저장됩니다.
     *
     * [fetch = FetchType.LAZY의 의미]
     * - LAZY (지연 로딩): Cart를 조회할 때 Member 정보는 즉시 가져오지 않습니다.
     * - member 필드에 실제로 접근할 때(예: cart.getMember()) 그때 DB에서 조회합니다.
     * - 성능 최적화: 불필요한 조인 쿼리를 줄여 성능을 향상시킵니다.
     *
     * 반대 개념: EAGER (즉시 로딩) - Cart 조회 시 Member도 무조건 함께 조회 (JOIN 발생)
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * [일대다(1:N) 관계 매핑]
     * @OneToMany: 하나의 Cart는 여러 개의 CartItem을 가질 수 있습니다.
     *
     * mappedBy = "cart":
     * - 이 관계의 주인은 CartItem 엔티티의 'cart' 필드라는 뜻입니다.
     * - 외래 키는 CartItem 테이블에 있습니다. (cart_id 컬럼)
     * - Cart 엔티티는 단순히 조회 목적으로만 이 관계를 사용합니다.
     *
     * cascade = CascadeType.ALL:
     * - Cart에 대한 모든 작업이 CartItem에도 전파됩니다.
     * - Cart를 저장(save)하면 → CartItem도 자동 저장
     * - Cart를 삭제(delete)하면 → CartItem도 자동 삭제
     * - 예: 회원이 탈퇴하여 장바구니가 삭제되면, 담긴 상품들도 모두 삭제되어야 합니다.
     *
     * orphanRemoval = true:
     * - "고아 객체 제거" 기능
     * - CartItem이 Cart의 cartItems 리스트에서 제거되면 DB에서도 자동 삭제됩니다.
     * - 예: cartItems.remove(item) → 해당 CartItem이 DB에서 DELETE됩니다.
     *
     * new ArrayList<>():
     * - NullPointerException 방지를 위해 빈 리스트로 초기화합니다.
     * - cartItems.add()를 호출할 때 null 에러가 나지 않습니다.
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * [정적 팩토리 메서드 패턴]
     *
     * new Cart()를 직접 호출하지 않고, Cart.create(member)로 객체를 생성합니다.
     *
     * 장점:
     * 1. 메서드 이름으로 의도를 명확히 표현 (createCart, ofMember 등)
     * 2. 생성 로직을 한 곳에서 관리 (생성 규칙이 바뀌어도 여기만 수정)
     * 3. 유효성 검증을 생성 시점에 수행 가능
     *
     * @param member 장바구니를 소유할 회원
     * @return 생성된 Cart 객체
     */
    public static Cart create(Member member) {
        Cart cart = new Cart();
        cart.member = member;
        return cart;
    }

    /**
     * [비즈니스 메서드: 장바구니에 상품 추가]
     *
     * 단순히 cartItems.add()를 외부에서 호출하지 않고,
     * Cart 엔티티가 자신의 상태를 관리하도록 메서드를 제공합니다.
     *
     * 이를 "캡슐화"라고 하며, 객체지향 설계의 핵심 원칙입니다.
     *
     * 양방향 관계 설정:
     * - cartItems.add(item): Cart → CartItem 방향 설정
     * - item.assignCart(this): CartItem → Cart 방향 설정
     * - 두 방향을 모두 설정해야 JPA가 올바르게 관계를 인식합니다.
     *
     * @param item 추가할 장바구니 아이템
     */
    public void addCartItem(CartItem item) {
        cartItems.add(item);
        item.assignCart(this); // CartItem에 이 Cart를 연결
    }

    /**
     * [비즈니스 메서드: 장바구니에서 상품 제거]
     *
     * orphanRemoval = true 옵션 덕분에:
     * - cartItems.remove(item)를 실행하면
     * - JPA가 자동으로 해당 CartItem을 DB에서 DELETE 합니다.
     *
     * @param item 제거할 장바구니 아이템
     */
    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
        item.assignCart(null); // 관계 해제
    }

    /**
     * [비즈니스 메서드: 장바구니 전체 비우기]
     *
     * 주문 완료 후 또는 사용자가 "장바구니 비우기"를 클릭했을 때 사용됩니다.
     *
     * clear() 호출 시 orphanRemoval이 동작하여 모든 CartItem이 DB에서 삭제됩니다.
     */
    public void clearCart() {
        cartItems.clear();
    }

    /**
     * [비즈니스 메서드: 장바구니 총 금액 계산]
     *
     * Java Stream API를 사용한 함수형 프로그래밍:
     * - cartItems.stream(): 리스트를 스트림으로 변환
     * - .mapToInt(): 각 CartItem을 int 값으로 변환
     * - CartItem::getTotalPrice: 메서드 레퍼런스 (item -> item.getTotalPrice()와 동일)
     * - .sum(): 모든 값을 더함
     *
     * 반복문으로 작성하면:
     * int total = 0;
     * for (CartItem item : cartItems) {
     *     total += item.getTotalPrice();
     * }
     * return total;
     *
     * @return 장바구니에 담긴 모든 상품의 총 금액
     */
    public int getTotalPrice() {
        return cartItems.stream()
                .mapToInt(CartItem::getTotalPrice)
                .sum();
    }

    /**
     * [비즈니스 메서드: 장바구니에 담긴 총 상품 개수]
     *
     * 주의: cartItems.size()가 아닙니다!
     * - size()는 상품 '종류' 개수 (예: 사과 1개, 바나나 1개 = 2)
     * - 우리가 원하는 것은 '수량' 합계 (예: 사과 3개 + 바나나 2개 = 5)
     *
     * @return 장바구니에 담긴 모든 상품의 총 수량
     */
    public int getTotalItemCount() {
        return cartItems.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}
