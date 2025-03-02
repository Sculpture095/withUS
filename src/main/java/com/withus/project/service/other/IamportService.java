package com.withus.project.service.other;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class IamportService {

    // 아임포트(PortOne) v1에서 발급받은 REST API 키/시크릿
    // (application.yml에 "iamport.v1.key", "iamport.v1.secret" 등으로 바꿔도 됨)
    @Value("${iamport.v1.apiKey}")
    private String impKey;

    @Value("${iamport.v1.apiSecret}")
    private String impSecret;

    // v1 API Endpoint
    private static final String V1_TOKEN_URL = "https://api.iamport.kr/users/getToken";
    private static final String V1_PAYMENTS_URL = "https://api.iamport.kr/payments/";

    /**
     * [v1] AccessToken 발급
     */
    public String getAccessTokenV1() {
        RestTemplate rest = new RestTemplate();

        // 1) Request Body: { "imp_key": "...", "imp_secret": "..." }
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("imp_key", impKey);
        bodyMap.put("imp_secret", impSecret);

        // 2) Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(bodyMap, headers);

        // 3) POST /users/getToken
        ResponseEntity<Map> response = rest.exchange(
                V1_TOKEN_URL,
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // 4) 응답 처리
        if (response.getStatusCode() == HttpStatus.OK) {
            Map resBody = response.getBody();
            // { "code": 0, "message": "SUCCESS", "response": { "access_token": "...", ... } }
            if (resBody != null && ((Number) resBody.get("code")).intValue() == 0) {
                Map data = (Map) resBody.get("response");
                return (String) data.get("access_token");  // 토큰 추출
            }
            throw new RuntimeException("v1 토큰 발급 실패: code=" + resBody.get("code")
                    + ", message=" + resBody.get("message"));
        }
        throw new RuntimeException("v1 토큰 발급 HTTP 오류: " + response.getStatusCodeValue());
    }

    /**
     * [v1] imp_uid 로 결제정보 조회
     */
    public Map<String, Object> getPaymentDataV1(String impUid, String accessToken) {
        RestTemplate rest = new RestTemplate();

        // GET /payments/{imp_uid}
        String url = V1_PAYMENTS_URL + impUid;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<Map> response = rest.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        System.out.println("URL = "+ V1_TOKEN_URL);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map resBody = response.getBody();
            // { "code": 0, "message": "SUCCESS", "response": {...} }
            if (resBody != null && ((Number) resBody.get("code")).intValue() == 0) {
                // 결제 정보는 "response" 키 안에 존재
                return (Map<String, Object>) resBody.get("response");
            }
            throw new RuntimeException("결제 정보 조회 실패: " + resBody.get("message"));
        }
        throw new RuntimeException("HTTP 오류: " + response.getStatusCodeValue());
    }


    @PostConstruct
    public void debugIamportKeys() {
        System.out.println("=== Debug IAMPORT V1 ===");
        System.out.println("impKey = " + impKey);
        System.out.println("impSecret = " + impSecret);
    }

}
