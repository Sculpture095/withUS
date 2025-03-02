package com.withus.project.dto.projects;

import com.withus.project.domain.projects.ContractStatus;
import com.withus.project.domain.projects.ProjectProgressStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompletedProjectDTO {

    private String projectId;      // 프로젝트 UUID (문자열)
    private String projectName;    // 프로젝트 이름
    private Double projectAmount;  // 프로젝트 예상 금액

    private String contractId;     // 계약 UUID (문자열)
    private Double contractAmount; // 실제 계약 금액
    private ContractStatus contractStatus;
    private ProjectProgressStatus progressStatus;
}
