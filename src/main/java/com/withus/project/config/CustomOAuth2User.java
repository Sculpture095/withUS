package com.withus.project.config;



import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * OAuth2 로그인 시, 구글에서 받아온 정보를
 * nickname / email 등으로 매핑해두기 위한 Custom OAuth2User
 */
public class CustomOAuth2User implements OAuth2User {

    private final Collection<? extends GrantedAuthority> authorities;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;

    private final String nickname;  // 우리가 템플릿에서 쓰고 싶은 필드
    private final String email;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes,
                            String nameAttributeKey,
                            String nickname,
                            String email) {
        this.authorities = authorities;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.nickname = nickname;
        this.email = email;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    /**
     * OAuth2User.getName() => 주로 "sub"(구글 user_id) 또는
     * ID 토큰에 해당하는 필드를 반환
     */
    @Override
    public String getName() {
        return (String) this.attributes.get(this.nameAttributeKey);
    }

    // 아래는 우리가 직접 만든 Getter
    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

}
