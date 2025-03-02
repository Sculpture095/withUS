package com.withus.project.controller;

import com.withus.project.config.CustomUserDetails;


import com.withus.project.dto.members.GoogleLoginDTO;
import com.withus.project.dto.members.MemberDTO;

import com.withus.project.service.member.MemberService;
import com.withus.project.util.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/members")
public class GoogleLoginController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider; // 토큰에서 이메일 추출을 위해 주입

    @PostMapping("/google-login")
    public ResponseEntity<String> googleLogin(@RequestBody GoogleLoginDTO googleLoginDTO,
                                              HttpSession session,
                                              HttpServletResponse response) {
        String token = googleLoginDTO.getToken();
        String email;
        try {
            // 1) 토큰에서 이메일 추출
            email = jwtTokenProvider.getEmailFromToken(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("토큰 파싱 실패");
        }

        // 2) DB에서 이메일로 사용자 조회
        MemberDTO member = memberService.getMemberByEmail(email);

        if (member != null) {
            // 3) 인증 객체 생성
            CustomUserDetails customUserDetails = CustomUserDetails.builder()
                    .username(member.getId())
                    .password(member.getPassword())  // 구글 로그인은 비번 의미 없음
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .nickname(member.getNickname())
                    .build();

            // 4) SecurityContext 에 Authentication 등록
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 5) JWT(선택) 발급
            String localToken = memberService.loginWithOAuthById(member.getId(), "GOOGLE");

            // 6) 세션에 정보 저장 (매우 중요)
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
            session.setAttribute("userToken", localToken);
            session.setAttribute("nickname", member.getNickname());
            session.setAttribute("member", member);
            session.setAttribute("pcaType", member.getPcaType());

            // 7) JSESSIONID 쿠키 설정
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60); // 1시간
            response.addCookie(cookie);

            return ResponseEntity.ok("구글 로그인 성공");
        } else {
            // 구글 로그인 성공했지만, 우리 서비스에 회원정보 없으면 회원가입 유도
            session.setAttribute("googleLoginInfo", googleLoginDTO);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("회원가입 필요: 추가 정보 입력");
        }
    }

}

