package com.withus.project.dto.members.parteners;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PartnerDTO {
    private Integer partnerIdx; // 파트너 ID 기본키 (요청용)
    private Integer memberIdx; // 회원 ID (요청용)
    private String degree; // 최종학력 (요청/응답 공용)
    private String schoolName; // 학교명 (요청/응답 공용)
    private String major; // 전공 (요청/응답 공용)
    private String admission; // 입학년도 (요청/응답 공용)
    private String graduation; // 졸업년도 (요청/응답 공용)
    private String location; // 거주 지역 (요청/응답 공용)
    private List<PortfolioDTO> portfolios; // 포트폴리오 리스트 (응답용)

}
