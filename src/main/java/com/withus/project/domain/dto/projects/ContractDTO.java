package com.withus.project.domain.dto.projects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ContractDTO {
    private Integer contractIdx; // 계약 ID 기본키
    private String contractId; // 고유식별자(응답용)
    private Integer partnerIdx; // 파트너 ID (요청용)
    private String projectId; // 프로젝트 ID (요청용)
    private String status; // 계약 상태 (요청/응답 공용)
    private Double amount; // 금액 (요청/응답 공용)
    private String detail; // 계약 내용 (요청용)
    private String contractName; // 계약명 (요청/응답 공용)
    private String contractDate; // 계약일 (응답용)
    private List<CaseDTO> caseEntities; // ✅ 고객 사례 리스트 추가
}
