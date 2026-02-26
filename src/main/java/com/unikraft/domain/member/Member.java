package com.unikraft.domain.member;

import com.unikraft.domain.cart.Cart;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Member 엔티티 (회원)
 *
 * [JPA 엔티티]
 * - 회원 정보를 저장하는 엔티티입니다.
 * - Member와 Cart는 1:1 관계입니다. (한 회원은 하나의 장바구니를 가짐)
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int age;
    @Column(unique = true)
    private String loginId;
    @Column(unique = true)
    private String email;
    private String password;
    private String address;

    /**
     * [일대일(1:1) 관계: Member → Cart]
     *
     * @OneToOne: Member와 Cart는 1:1 관계입니다.
     *
     * mappedBy = "member":
     * - 이 관계의 주인은 Cart 엔티티의 'member' 필드입니다.
     * - 외래 키는 Cart 테이블에 있습니다. (member_id 컬럼)
     * - Member는 단순히 조회 용도로만 이 관계를 사용합니다.
     *
     * cascade = CascadeType.ALL:
     * - Member에 대한 모든 작업이 Cart에도 전파됩니다.
     * - Member를 삭제하면 Cart도 자동 삭제됩니다.
     * - 예: 회원 탈퇴 시 장바구니도 함께 삭제됩니다.
     *
     * orphanRemoval = true:
     * - Member에서 Cart를 제거하면 DB에서도 자동 삭제됩니다.
     *
     * fetch = FetchType.LAZY:
     * - Member 조회 시 Cart를 즉시 가져오지 않습니다.
     * - member.getCart()처럼 실제로 접근할 때 DB에서 조회합니다.
     */
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Cart cart;

    @Builder
    public Member(String name, int age, String loginId, String email, String password, String address){
        this.name = name;
        this.age = age;
        this.loginId = loginId;
        this.email = email;
        this.password = password;
        this.address = address;
    }

    /**
     * [비즈니스 메서드: 장바구니 연결]
     *
     * Member와 Cart의 양방향 관계를 설정합니다.
     * - Member → Cart: this.cart = cart
     * - Cart → Member: Cart 생성 시 이미 설정됨
     *
     * 사용 시점:
     * - 회원 가입 후 장바구니를 생성할 때
     * - 기존 회원에게 장바구니를 연결할 때
     *
     * @param cart 연결할 장바구니
     */
    public void assignCart(Cart cart) {
        this.cart = cart;
    }
}