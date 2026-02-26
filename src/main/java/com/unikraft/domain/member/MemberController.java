package com.unikraft.domain.member;

import com.unikraft.domain.member.dto.LoginRequest;
import com.unikraft.domain.member.dto.SignUpRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = memberService.login(loginRequest.getLoginId(), loginRequest.getPassword());
        return ResponseEntity.ok(token);
    }

    /**
     * 내 정보 조회 API
     * @param loginId @AuthenticationPrincipal을 통해 SecurityContextHolder에서 가져온 사용자 정보
     * @return 로그인한 사용자의 ID
     */
    @GetMapping("/me")
    public ResponseEntity<String> getMyInfo(@AuthenticationPrincipal String loginId) {
        return ResponseEntity.ok("내 정보: " + loginId);
    }

    /**
     * 회원가입 API
     * @param signUpRequest 프론트엔드에서 보낸 회원가입 정보 (JSON)
     * @return 생성된 회원의 ID와 함께 201 Created 상태 코드
     */
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody SignUpRequest signUpRequest) {
        // 1. @Valid 어노테이션 추가
        //    - SignUpRequest DTO에 정의된 검증 규칙(@NotBlank, @Size 등)을 검사합니다.
        //    - 검증에 실패하면 MethodArgumentNotValidException이 발생하고, 400 Bad Request 에러가 응답됩니다.
        
        Long memberId = memberService.signUp(
                signUpRequest.getLoginId(),
                signUpRequest.getPassword(),
                signUpRequest.getName(),
                signUpRequest.getEmail()
        );

        URI location = URI.create("/api/members/" + memberId);
        return ResponseEntity.created(location).build();
    }
}