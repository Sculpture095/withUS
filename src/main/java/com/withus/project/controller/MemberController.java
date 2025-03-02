package com.withus.project.controller;

import com.withus.project.config.CustomUserDetails;
import com.withus.project.dto.members.MemberDTO;
import com.withus.project.service.member.AuthService;
import com.withus.project.service.member.MemberService;
import com.withus.project.service.member.MyPageService;
import com.withus.project.service.member.PasswordService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;
    private final PasswordService passwordService;
    private final MyPageService myPageService;

    // 회원가입 처리 (JSON 요청)
    @PostMapping("/signup")
    public ResponseEntity<String> registerMember(@RequestBody MemberDTO dto) {
        try {
            // ✅ 회원 정보 저장
            String memberId = memberService.registerMember(dto);

            // ✅ 회원 가입 후, 기본 마이페이지 데이터 생성
            myPageService.createDefaultMyPage(memberId);

            return ResponseEntity.ok("회원 가입 성공: ID = " + memberId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/signup")
    public String signupForm(HttpServletRequest request, Model model) {
        // 기존의 회원가입 폼 처리 로직 유지
        // 세션에 저장된 alert 메시지가 있으면 모델에 추가 후 세션에서 제거
        String alertMessage = (String) request.getSession().getAttribute("alertMessage");
        if (alertMessage != null) {
            model.addAttribute("alertMessage", alertMessage);
            request.getSession().removeAttribute("alertMessage");
        }
        return "signup"; // templates/signup.html
    }

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberDTO dto, HttpServletResponse response, HttpSession session) {
        log.info("로그인 요청 - 아이디: {}", dto.getId());

        try {
            // DB에서 해당 아이디의 회원 정보를 조회
            MemberDTO loggedInMember = memberService.getMemberById(dto.getId());
            if (loggedInMember == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인 실패: 존재하지 않는 사용자입니다.");
            }

            // 입력된 비밀번호와 DB에 저장된 암호화된 비밀번호 비교
            if (!passwordService.checkPassword(dto.getPassword(), loggedInMember.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("로그인 실패: 비밀번호가 올바르지 않습니다.");
            }

            // 비밀번호 일치 시, 토큰 발급 및 세션에 정보 저장
            String token = memberService.loginWithOAuthById(dto.getId(), "LOCAL");
            session.setAttribute("userToken", token);
            session.setAttribute("nickname", loggedInMember.getNickname());
            session.setAttribute("member", loggedInMember);
            session.setAttribute("pcaType", loggedInMember.getPcaType());

            // CustomUserDetails 객체 생성 (ROLE_USER 권한 및 닉네임 포함)
            CustomUserDetails customUserDetails = CustomUserDetails.builder()
                    .username(loggedInMember.getId())
                    .password(loggedInMember.getPassword())
                    .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                    .nickname(loggedInMember.getNickname())
                    .build();

            // Authentication 객체 생성 및 SecurityContext에 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // JSESSIONID 쿠키 설정
            Cookie cookie = new Cookie("JSESSIONID", session.getId());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(60 * 60); // 1시간 유지
            response.addCookie(cookie);

            log.info("로그인 성공 - 세션 저장 완료: {}", loggedInMember);
            return ResponseEntity.ok("로그인 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 실패: " + e.getMessage());
        }
    }


















    /**
     * 아이디, 닉네임, 이메일, 전화번호 중복 확인 API
     * - 신규 가입 시: currentId 없이 호출 → DB에 값이 있으면 중복 에러
     * - 프로필 수정 시: currentId를 전달 → 자신의 정보는 제외하고 중복 체크
     */
    @GetMapping("/validate")
    public ResponseEntity<String> validateMember(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String currentId) {
        try {
            if (currentId == null || currentId.isEmpty()) {
                // 신규 회원가입 시
                memberService.validateDuplicateMember(id, nickname, email, phone);
            } else {
                // 프로필 수정 시: 자신의 정보는 중복 검사에서 제외
                memberService.validateDuplicateMemberExcludingCurrent(id, nickname, email, phone, currentId);
            }
            return ResponseEntity.ok("사용 가능한 정보입니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
