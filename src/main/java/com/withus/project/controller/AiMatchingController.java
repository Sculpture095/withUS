package com.withus.project.controller;

import com.withus.project.service.AiMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiMatchingController {

    private final AiMatchingService aiMatchingService;

    /**
     * GET /api/ai/recommend/{projectId}
     * 외부에 노출할 고유 식별자(projectId)를 받아 AI 추천 결과를 반환합니다.
     */
    @GetMapping("/recommend/{projectId}")
    public ResponseEntity<String> recommendPartner(@PathVariable String projectId) {
        String recommendation = aiMatchingService.getPartnerRecommendation(projectId);
        System.out.println("===== AI Response Start =====");
        System.out.println(recommendation);
        System.out.println("===== AI Response End =====");
        return ResponseEntity.ok(recommendation);
    }
}
