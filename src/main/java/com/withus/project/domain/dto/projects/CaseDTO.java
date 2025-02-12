package com.withus.project.domain.dto.projects;

import com.withus.project.domain.dto.members.SelectSkillDTO;
import com.withus.project.domain.members.SelectSkillEntity;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CaseDTO {
    private Integer caseIdx; // 케이스 ID 기본키 (요청용)
    private String caseId; // UUID 기본키 (응답용)
    private String contractId; // 계약 ID (요청용)
    private String title; // 제목 (요청/응답 공용)
    private String content; // 내용 (요청/응답 공용)
    private String caseImg; // 이미지 URL (요청/응답 공용)
    private String thumbnail; // 썸네일 URL (요청/응답 공용)
    private Double caseAmount; // 계약 금액 (요청/응답 공용)
    private String timeline; // 프로젝트 일정 (요청/응답 공용)
    private String rating; // 평가 (요청/응답 공용)
    private String createDate; // 작성일 (응답용)
    private List<SelectSkillDTO> relatedTechs; // 관련 기술 (요청/응답 공용)
}
