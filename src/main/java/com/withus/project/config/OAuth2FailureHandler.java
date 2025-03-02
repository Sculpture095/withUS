package com.withus.project.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        if (exception instanceof OAuth2AuthenticationException) {
            OAuth2AuthenticationException oae = (OAuth2AuthenticationException) exception;
            // 에러 코드 확인
            String errorCode = oae.getError().getErrorCode();
            if ("member_not_found".equals(errorCode)) {
                // 세션에 에러 메시지를 저장
                request.getSession().setAttribute("alertMessage", "회원 정보가 존재하지 않습니다. 회원가입해주세요.");
                response.sendRedirect("/signup");
                return;
            }
        }
        response.sendRedirect("/login?error");
    }
}



