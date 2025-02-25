package com.withus.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.withus.project.domain.dto.AiRecommendResponse;
import com.withus.project.service.AiMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiMatchingController {

    private final AiMatchingService aiMatchingService;

    /**
     * GET /api/ai/recommend/{projectId}
     * 외부에 노출할 고유 식별자(projectId)를 받아 AI 추천 결과를 반환합니다.
     */
    @PostMapping("/recommend/{projectId}")
    public AiRecommendResponse  recommendPartner(@PathVariable String projectId) throws JsonProcessingException {
        String recommendation = aiMatchingService.getPartnerRecommendation(projectId);

        ObjectMapper mapper = new ObjectMapper();
        AiRecommendResponse response = mapper.readValue(recommendation,AiRecommendResponse.class);


        return response;
    }
}
