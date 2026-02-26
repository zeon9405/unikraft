package com.unikraft.domain.member;

import com.unikraft.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // 1. PasswordEncoder 의존성 주입

    /**
     * 회원가입 (Sign Up)
     * @param loginId 사용자 ID
     * @param password 사용자 비밀번호 (평문)
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @return 생성된 회원의 ID
     */
    @Transactional // 데이터를 저장하므로 쓰기 모드 트랜잭션 필요
    public Long signUp(String loginId, String password, String name, String email) {

        // 1. 중복 검사 (선택 사항이지만 권장)
        //    - 이미 존재하는 loginId나 email로 가입하려는지 확인합니다.
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 2. 비밀번호 암호화
        //    - 사용자가 입력한 평문 비밀번호를 BCrypt 알고리즘으로 암호화합니다.
        //    - 암호화된 비밀번호는 복호화가 불가능합니다.
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 회원 엔티티 생성 및 저장
        Member member = Member.builder()
                .loginId(loginId)
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .name(name)
                .email(email)
                .build();

        memberRepository.save(member);

        return member.getId();
    }

    /**
     * 로그인 (Login)
     * @param loginId 사용자 ID
     * @param password 사용자 비밀번호 (평문)
     * @return 로그인 성공 시, 생성된 JWT 토큰을 반환합니다.
     */
    public String login(String loginId, String password) {

        // 1. 회원 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 아이디입니다."));

        // 2. 비밀번호 일치 확인 (암호화된 비밀번호 비교)
        //    - passwordEncoder.matches(평문, 암호화된값) 메서드를 사용해야 합니다.
        //    - 내부적으로 평문을 암호화하여 DB의 값과 비교합니다.
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성
        return jwtUtil.createToken(member.getLoginId());
    }
}