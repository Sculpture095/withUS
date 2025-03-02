package com.withus.project.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AiRecommendResponse {

    private String recommendedNickname;  // 추천 파트너 닉네임
    private String reason;              // 이유
    private List<String> skills;          // 주요 기술
    private String career;             // 경력
}
