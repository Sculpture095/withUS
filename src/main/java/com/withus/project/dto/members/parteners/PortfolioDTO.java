package com.withus.project.dto.members.parteners;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class PortfolioDTO {
    private Integer portfolioIdx; // 포트폴리오 ID 기본키 (요청용)
    private String portfolioId; // UUID 기본키 (응답용)
    private Integer partnerIdx; // 파트너 ID (요청용)
    private String portfolioTitle; // 제목 (요청/응답 공용)
    private String portfolioContext; // 설명 (요청/응답 공용)
    private Boolean publicOk; // 공개 여부 (요청/응답 공용)
    private String startDate; // 시작일자 (요청/응답 공용, yyyy-MM-dd 형식)
    private String endDate; // 종료일자 (요청/응답 공용, yyyy-MM-dd 형식)
    private String portfolioImg; // 이미지 URL (요청/응답 공용)
    private String thumbnail; // 썸네일 URL (요청/응답 공용)
    private String resultUrl; // 결과 URL (요청용)
}
