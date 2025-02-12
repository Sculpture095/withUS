package com.withus.project.domain.dto.projects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SelectProjectDTO {
    private Integer selectProjectIdx; // 선택한 프로젝트 ID 기본키
    private String selectProjectId; //고유식별자v
    private Integer partnerIdx; // 파트너 식별 번호
    private String projectId; // 프로젝트 UUID
    private Boolean yn; // 클라이언트 선호 여부
}
