package com.withus.project.dto.projects;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OngoingProjectDTO {

    private String projectId;    // 프로젝트의 UUID (문자열)
    private String projectName;  // 프로젝트명
    private Double projectAmount; // (프로젝트 테이블에 있는 amount)

    private String contractId;   // 계약의 UUID (문자열)
    private Double contractAmount; // 실제 계약 금액
    private String status;       // 계약 상태 (SIGNED 등)
    private String progressStatus; // 결제 상태

}
