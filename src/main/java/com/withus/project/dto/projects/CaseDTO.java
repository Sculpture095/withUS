package com.withus.project.dto.projects;

import com.withus.project.dto.members.SelectSkillDTO;
import lombok.*;

import java.util.List;

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
    private String shortContent; //짧은 후기
    private String caseImg;
    private Double rating; // 평가 (요청/응답 공용)
    private String createDate; // 작성일 (응답용)
    private List<SelectSkillDTO> relatedTechs; // 관련 기술 (요청/응답 공용)
}
