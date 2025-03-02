package com.withus.project.config;

import com.withus.project.repository.members.MemberRepositoryImpl;
import com.withus.project.service.other.CustomOAuth2UserService;
import com.withus.project.service.other.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    /**
     * PasswordEncoder Bean 등록
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security Filter Chain 설정
     *
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     * @throws Exception 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   OAuth2FailureHandler oAuth2FailureHandler,
                                                   CustomOAuth2UserService customOAuth2UserService) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/check-login", "/uploads/**", "/css/**", "/js/**", "/images/**", "/error", "/login", "/signup").permitAll()
                        // 예: 특정 경로는 로그인(ROLE_USER) 필요
                        .requestMatchers("/partner/**", "/p_myPage/**", "/c_myPage/**").hasRole("USER")
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"loggedIn\": false, \"message\": \"Unauthorized\"}");
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true)
                )
                .headers(headers -> headers.cacheControl(cache -> cache.disable()))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(remember -> remember.disable())
                .formLogin(form -> form
                        .loginPage("/login")           // 커스텀 로그인 페이지
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")                // OAuth2 로그인 시작 시 보여줄 페이지
                        .failureHandler(oAuth2FailureHandler)
                        .defaultSuccessUrl("/main", true)
                        // 중요: 여기에 CustomOAuth2UserService 등록
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                )
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService(MemberRepositoryImpl memberRepository) {
        return new CustomUserDetailsService(memberRepository);
    }




    /**
     * In-Memory 사용자 정의 (테스트용)
     *
     * @return InMemoryUserDetailsManager
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsServices(PasswordEncoder passwordEncoder) {
        UserDetails user = User.withUsername("admin")
                .password(passwordEncoder.encode("admin123")) // 비밀번호 암호화
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8090", "http://localhost:3000")); // ✅ 프론트엔드 도메인 추가
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // ✅ 쿠키 포함 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }







}
