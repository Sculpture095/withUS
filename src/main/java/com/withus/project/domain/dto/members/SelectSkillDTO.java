package com.withus.project.domain.dto.members;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SelectSkillDTO {
    private Integer selectSkillIdx; // 선택한 기술 ID 기본키 (요청용)
    private String selectSkillId; // 선택한 기술 고유 식별 번호

    private String projectId; // 프로젝트 ID 추가
    private String skillCode; // 기술 코드 (SkillType Enum의 이름)
    private Integer careerDuration; // 경력 기간


    public SelectSkillDTO(Integer selectSkillIdx, String selectSkillId, String skillCode,  String projectId) {
        this.selectSkillIdx = selectSkillIdx;
        this.selectSkillId = selectSkillId;
        this.skillCode = skillCode;
        this.projectId = projectId;
    }
}
