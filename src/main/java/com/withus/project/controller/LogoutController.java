package com.withus.project.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutController {

    @GetMapping("/logoutPage")
    public String logoutPage(HttpSession session, HttpServletResponse response) {
        if (session != null) {
            session.invalidate(); // ✅ 세션 무효화
        }

        // ✅ JSESSIONID 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "logout"; // ✅ logout.html로 이동
    }
}
