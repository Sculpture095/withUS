package com.withus.project.service.other;

import com.withus.project.config.CustomOAuth2User;
import com.withus.project.dto.members.MemberDTO;
import com.withus.project.service.member.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 로그인 성공 후, 구글에서 받아온 사용자 정보를
 * DB 회원 정보와 매핑하여 CustomOAuth2User를 반환한다.
 */
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberService memberService;
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    public CustomOAuth2UserService(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1) 구글에서 기본 userInfo (email, name, picture 등) 받기
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());

        // 2) 구글 계정 정보에서 이메일 추출
        String email = (String) attributes.get("email");
        if (email == null) {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_token"), "구글에서 이메일 정보를 가져올 수 없습니다.");
        }

        // 3) DB에서 해당 이메일로 회원 조회
        MemberDTO member;
        try {
            member = memberService.getMemberByEmail(email);
        } catch (IllegalArgumentException e) {
            OAuth2Error error = new OAuth2Error("member_not_found", "회원 정보가 존재하지 않습니다.", null);
            throw new OAuth2AuthenticationException(error, "회원 정보가 존재하지 않습니다.");
        }

        // 4) nickname 설정 (DB 닉네임, 구글 프로필 name)
        String googleName = (String) attributes.get("name");
        String finalNickname = (member.getNickname() != null) ? member.getNickname() : googleName;

        // 5) "sub" 필드를 ID로 사용 (OAuth2User.getName() 결과)
        String nameAttributeKey = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // 보통 "sub"

        // 6) 커스텀 OAuth2User 생성
        CustomOAuth2User customUser = new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                nameAttributeKey,
                finalNickname,
                email
        );

        // ================================
        // 7) 세션에 member 정보를 직접 저장 (주의)
        // ================================
        try {
            ServletRequestAttributes sra =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (sra != null) {
                HttpServletRequest request = sra.getRequest();
                HttpSession session = request.getSession(true);

                // 일반 로그인과 동일하게 세션에 정보 저장
                session.setAttribute("member", member);
                session.setAttribute("nickname", member.getNickname());
                session.setAttribute("pcaType", member.getPcaType());
            }
        } catch (Exception e) {
            // 예: RequestContextHolder에 request가 없는 환경일 수도 있으니 예외 처리
            System.err.println("세션에 member 정보 저장 실패: " + e.getMessage());
        }

        // 8) 반환
        return customUser;
    }
}

