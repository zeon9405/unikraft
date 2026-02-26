package com.unikraft.domain.cart;

import com.unikraft.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CartItem 엔티티 (장바구니 아이템)
 *
 * [JPA - Java Persistence API]
 * - 장바구니에 담긴 개별 상품을 나타내는 엔티티입니다.
 * - Cart와 Product를 연결하는 "중간 테이블" 역할을 합니다.
 *
 * [설계 개념]
 * - 하나의 Cart는 여러 개의 CartItem을 가질 수 있습니다. (N:1 관계)
 * - 하나의 Product는 여러 CartItem에서 참조될 수 있습니다. (N:1 관계)
 * - CartItem은 수량(quantity) 정보를 추가로 가집니다.
 *
 * 예시:
 * Cart(회원A의 장바구니)
 *   ├─ CartItem(사과, 3개)  → Product(사과)
 *   └─ CartItem(바나나, 2개) → Product(바나나)
 *
 * Cart(회원B의 장바구니)
 *   └─ CartItem(사과, 5개)  → Product(사과)  ← 같은 상품을 다른 회원이 담을 수 있음
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자
@Getter
public class CartItem {

    /**
     * [Primary Key]
     * - 각 CartItem을 고유하게 식별하는 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * [다대일(N:1) 관계: CartItem → Cart]
     *
     * @ManyToOne: 여러 개의 CartItem이 하나의 Cart에 속합니다.
     *
     * @JoinColumn(name = "cart_id"):
     * - 데이터베이스에 'cart_id' 컬럼이 생성됩니다.
     * - 이 컬럼은 Cart 테이블의 id를 참조하는 외래 키(Foreign Key)입니다.
     *
     * fetch = FetchType.LAZY:
     * - CartItem 조회 시 Cart 정보를 즉시 가져오지 않습니다.
     * - cartItem.getCart()처럼 실제로 접근할 때 DB에서 조회합니다.
     * - 성능 최적화를 위해 LAZY 로딩을 기본으로 사용합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    /**
     * [다대일(N:1) 관계: CartItem → Product]
     *
     * - 여러 개의 CartItem이 하나의 Product를 참조할 수 있습니다.
     * - 예: 회원A의 장바구니에 "사과", 회원B의 장바구니에도 "사과"
     *
     * @JoinColumn(name = "product_id"):
     * - 데이터베이스에 'product_id' 컬럼이 생성됩니다.
     * - Product 테이블의 id를 참조합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * [수량 필드]
     *
     * - 장바구니에 담긴 상품의 개수를 저장합니다.
     * - 단순히 Product만 저장하면 몇 개를 담았는지 알 수 없습니다.
     * - 이것이 CartItem을 별도 엔티티로 분리한 핵심 이유입니다!
     */
    private int quantity;

    /**
     * [정적 팩토리 메서드: CartItem 생성]
     *
     * new CartItem()을 직접 호출하지 않고, 이 메서드를 통해 객체를 생성합니다.
     *
     * 장점:
     * 1. 생성 로직이 한 곳에 집중됩니다.
     * 2. 메서드 이름으로 의도를 명확히 표현합니다. (create, of 등)
     * 3. 유효성 검증을 추가하기 쉽습니다.
     *
     * @param product 장바구니에 담을 상품
     * @param quantity 담을 수량
     * @return 생성된 CartItem 객체
     */
    public static CartItem create(Product product, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.product = product;
        cartItem.quantity = quantity;
        return cartItem;
    }

    /**
     * [양방향 관계 설정 메서드]
     *
     * Cart와 CartItem은 양방향 관계입니다:
     * - Cart → CartItem: cartItems 리스트
     * - CartItem → Cart: cart 필드
     *
     * 양쪽 모두 설정해야 JPA가 올바르게 동작합니다.
     * 이 메서드는 Cart 엔티티의 addCartItem()에서 호출됩니다.
     *
     * 사용 예:
     * Cart cart = ...;
     * CartItem item = CartItem.create(product, 3);
     * cart.addCartItem(item);  // 내부에서 item.assignCart(this) 호출
     *
     * @param cart 이 CartItem이 속할 Cart
     */
    public void assignCart(Cart cart) {
        this.cart = cart;
    }

    /**
     * [비즈니스 메서드: 수량 변경]
     *
     * 사용자가 장바구니에서 수량을 변경할 때 사용합니다.
     *
     * 왜 setter를 쓰지 않고 별도 메서드를 만드나요?
     * - setQuantity(int quantity)보다 changeQuantity(int quantity)가 의도가 명확합니다.
     * - 나중에 유효성 검증 로직을 추가하기 쉽습니다.
     *   예: if (quantity < 1) throw new IllegalArgumentException("수량은 1개 이상이어야 합니다");
     * - 비즈니스 로직을 엔티티에 응집시키는 도메인 주도 설계(DDD) 원칙입니다.
     *
     * @param quantity 변경할 수량
     */
    public void changeQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * [비즈니스 메서드: 수량 증가]
     *
     * 사용자가 같은 상품을 또 담을 때 사용합니다.
     *
     * 시나리오:
     * 1. 장바구니에 "사과 3개"가 이미 있음
     * 2. 사용자가 "사과 2개"를 추가로 담음
     * 3. 새로운 CartItem을 생성하지 않고, 기존 CartItem의 수량을 5개로 증가
     *
     * @param quantity 증가시킬 수량
     */
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    /**
     * [비즈니스 메서드: 이 아이템의 총 금액 계산]
     *
     * 상품 단가 × 수량 = 총 금액
     *
     * 예:
     * - 사과 1개 = 1000원
     * - 수량 = 3개
     * - 총 금액 = 1000 × 3 = 3000원
     *
     * 이 메서드는 Cart 엔티티의 getTotalPrice()에서 사용됩니다.
     *
     * @return 상품 가격 × 수량
     */
    public int getTotalPrice() {
        return product.getPrice() * quantity;
    }

    /**
     * [비즈니스 메서드: 재고 확인]
     *
     * 장바구니에 담으려는 수량이 재고보다 많은지 확인합니다.
     *
     * 사용 시나리오:
     * 1. 사용자가 장바구니에 상품 담기
     * 2. 주문 직전에 재고 확인
     * 3. 재고 부족 시 에러 메시지 표시
     *
     * @return 재고가 충분하면 true, 부족하면 false
     */
    public boolean isStockAvailable() {
        return product.getStockQuantity() >= quantity;
    }

    /**
     * [비즈니스 메서드: 동일 상품인지 확인]
     *
     * 장바구니에 이미 같은 상품이 있는지 확인할 때 사용합니다.
     *
     * 시나리오:
     * 1. 사용자가 "사과"를 장바구니에 추가
     * 2. 장바구니에 이미 "사과"가 있는지 확인
     * 3. 있으면 수량만 증가, 없으면 새 CartItem 생성
     *
     * Product 객체를 직접 비교하는 것이 아니라, ID를 비교하는 것이 안전합니다.
     * (JPA 프록시 객체 문제를 피하기 위함)
     *
     * @param product 비교할 상품
     * @return 같은 상품이면 true, 다르면 false
     */
    public boolean isSameProduct(Product product) {
        return this.product.getId().equals(product.getId());
    }
}
