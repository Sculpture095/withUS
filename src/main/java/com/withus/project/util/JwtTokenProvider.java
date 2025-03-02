package com.withus.project.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.springframework.security.config.Elements.JWT;

@Component
public class JwtTokenProvider {

    // 충분히 긴 키를 사용하세요. HS256의 경우 최소 32바이트가 필요합니다.
    private final String secretKey = "Dk9!s3V#b2L&n7Zq4Y@x6P%m1Jf8T*r0";
    private final long validityInMilliseconds = 3600000; // 1시간

    // secretKey 문자열을 기반으로 SecretKey 객체 생성
    private final SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

    /**
     * JWT 토큰 생성
     *
     * @param userId 사용자 ID
     * @param role   사용자 역할 (ex: PASSWORD_RESET, OAUTH_PROVIDER)
     * @return JWT 토큰
     */
    public String createToken(String userId, String role) {
        Claims claims = Jwts.claims().setSubject(userId);
        claims.put("role", role); // 사용자 역할 추가

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256) // SecretKey와 알고리즘 사용
                .compact();
    }

    /**
     * JWT 토큰에서 사용자 ID 추출
     *
     * @param token JWT 토큰
     * @return 사용자 ID
     */
    public String getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * JWT 토큰 유효성 검증
     *
     * @param token JWT 토큰
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            // JWT 토큰은 "header.payload.signature" 형식이므로, 점(.)을 기준으로 분리합니다.
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("잘못된 JWT 토큰 형식입니다.");
            }
            // 두 번째 부분(payload)을 디코딩합니다.
            String payload = parts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payload);
            String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);

            // Jackson을 이용해 JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(decodedPayload);
            JsonNode emailNode = jsonNode.get("email");
            if (emailNode == null || emailNode.asText().isEmpty()) {
                throw new IllegalArgumentException("토큰 내에 이메일 정보가 없습니다.");
            }
            return emailNode.asText().trim().toLowerCase();
        } catch (Exception e) {
            throw new IllegalArgumentException("토큰에서 이메일 추출에 실패했습니다.", e);
        }
    }

}
