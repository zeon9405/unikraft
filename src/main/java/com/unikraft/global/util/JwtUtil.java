package com.unikraft.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 1. 비밀키 (Secret Key)
    //    - 토큰을 서명(Sign)하고 검증(Verify)할 때 사용하는 열쇠입니다.
    //    - 외부로 유출되면 누구나 토큰을 위조할 수 있으므로 절대 공개되면 안 됩니다.
    //    - 실무에서는 application.yml 파일에서 불러와야 하지만, 지금은 하드코딩으로 진행합니다.
    //    - HS256 알고리즘을 사용하기 위해 충분히 긴 문자열(32byte 이상)이어야 합니다.
    private static final String SECRET_KEY = "unikraft_secret_key_must_be_very_long_and_secure_enough_to_prevent_brute_force_attacks";
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 2. 토큰 만료 시간 (Expiration Time)
    //    - 토큰이 발급된 후 언제까지 유효한지 설정합니다.
    //    - 여기서는 1시간(60분 * 60초 * 1000밀리초)으로 설정했습니다.
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * 토큰 생성 (Create Token)
     * @param email 사용자 이메일 (토큰의 주체, Subject)
     * @return 생성된 JWT 문자열
     */
    public String createToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(email) // 토큰의 주인(Subject) 설정
                .setIssuedAt(now) // 토큰 발급 시간 설정
                .setExpiration(expiryDate) // 토큰 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256) // 비밀키와 알고리즘으로 서명
                .compact(); // 토큰 생성 및 직렬화
    }

    /**
     * 토큰에서 이메일 추출 (Get Email from Token)
     * @param token JWT 문자열
     * @return 토큰에 담긴 이메일 (Subject)
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key) // 비밀키로 서명 검증
                .build()
                .parseClaimsJws(token) // 토큰 파싱
                .getBody(); // Payload(Claims) 추출

        return claims.getSubject(); // Subject(이메일) 반환
    }

    /**
     * 토큰 유효성 검증 (Validate Token)
     * @param token JWT 문자열
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            // 서명이 틀리거나, 만료되었거나, 형식이 잘못된 경우 예외 발생
            return false;
        }
    }
}