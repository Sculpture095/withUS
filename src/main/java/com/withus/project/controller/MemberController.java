package com.withus.project.controller;

import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.service.member.AuthService;
import com.withus.project.service.member.MemberService;
import com.withus.project.service.member.MyPageService;
import com.withus.project.service.member.PasswordService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberDTO dto, HttpSession session) {
        System.out.println("로그인 요청 - 아이디: " + dto.getId());
        try {
            // id 기반 로그인 처리
            String token = memberService.loginWithOAuthById(dto.getId(), "LOCAL");
            session.setAttribute("userToken", token);

            // 로그인한 회원 정보 가져오기
            MemberDTO loggedInMember = memberService.getMemberById(dto.getId());

            // 닉네임 저장
            session.setAttribute("nickname", loggedInMember.getNickname());

            // ✅ 세션에 member 객체 저장 (해결책)
            session.setAttribute("member", loggedInMember);

            // ✅ pcaType도 세션에 저장
            session.setAttribute("pcaType", loggedInMember.getPcaType());

            System.out.println("로그인 성공 - member 저장됨: " + loggedInMember); // 디버깅용 로그

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
