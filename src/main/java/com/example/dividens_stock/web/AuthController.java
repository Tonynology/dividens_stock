package com.example.dividens_stock.web;


import com.example.dividens_stock.model.Auth;
import com.example.dividens_stock.model.MemberEntity;
import com.example.dividens_stock.security.TokenProvider;
import com.example.dividens_stock.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
        MemberEntity result = this.memberService.register(request);
        log.info("회원 가입 요청: " + result);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
        // 로그린용 API
        // 패스워드 인증, 토큰 생성반환.
        MemberEntity member = this.memberService.authenticate(request);
        String token = this.tokenProvider.generateToken(member.getUsername(), member.getRoles());
        log.info("로그인한 회원 정보와 패스워드 토큰 정보: " + member + " 패스워드 토큰: " + token);

        return ResponseEntity.ok(token);
    }
}
