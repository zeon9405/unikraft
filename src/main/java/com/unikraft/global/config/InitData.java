package com.unikraft.global.config;

import com.unikraft.domain.member.Member;
import com.unikraft.domain.member.MemberRepository;
import com.unikraft.domain.product.Product;
import com.unikraft.domain.product.ProductCategory;
import com.unikraft.domain.product.ProductCategoryRepository;
import com.unikraft.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitData implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ProductCategoryRepository productCategoryRepository; // 1. [추가] 카테고리 리포지토리 주입
    private final PasswordEncoder passwordEncoder; // 2. [추가] 비밀번호 암호화기 주입

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. 테스트용 회원 데이터 생성
        if (memberRepository.count() == 0) {
            Member member = Member.builder()
                    .name("테스트유저")
                    .email("test@test.com")
                    .loginId("testuser")
                    .password(passwordEncoder.encode("1234")) // 3. [수정] 비밀번호 암호화 적용
                    .build();
            memberRepository.save(member);
        }

        // 2. 테스트용 카테고리 데이터 생성 (먼저 생성해야 함)
        if (productCategoryRepository.count() == 0) {
            ProductCategory teaCategory = new ProductCategory("TEA");
            productCategoryRepository.save(teaCategory);

            ProductCategory dessertCategory = new ProductCategory("DESSERT");
            productCategoryRepository.save(dessertCategory);

            // 3. 테스트용 상품 데이터 생성
            //    - 위에서 생성한 카테고리 객체를 사용하여 상품과 연결합니다.
            if (productRepository.count() == 0) {
                productRepository.save(Product.builder()
                        .name("녹차")
                        .price(5000)
                        .description("맛있는 녹차")
                        .imageUrl("tea.jpg")
                        .category(teaCategory) // [수정] 카테고리 객체 주입
                        .stockQuantity(100) // [추가] 재고 수량 설정
                        .build());

                productRepository.save(Product.builder()
                        .name("초코 케이크")
                        .price(7000)
                        .description("달콤한 케이크")
                        .imageUrl("cake.jpg")
                        .category(dessertCategory) // [수정] 카테고리 객체 주입
                        .stockQuantity(50) // [추가] 재고 수량 설정
                        .build());
            }
        }
    }
}