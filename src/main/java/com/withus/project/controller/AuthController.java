package com.withus.project.controller;

import com.withus.project.domain.dto.members.MemberDTO;
import com.withus.project.domain.members.MemberEntity;
import com.withus.project.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {

    private final MemberService memberService;

    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Object>> checkLogin(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // ✅ 세션에 저장된 사용자 정보 확인
        MemberDTO loggedInMember = (MemberDTO) session.getAttribute("member");

        if (loggedInMember != null) {
            response.put("loggedIn", true);
            response.put("nickname", loggedInMember.getNickname());
            System.out.println("✅ 로그인 확인 - 사용자: " + loggedInMember.getNickname());
            return ResponseEntity.ok(response);
        }

        response.put("loggedIn", false);
        System.out.println("🚨 로그인되지 않음 - 세션 정보 없음.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }













}
