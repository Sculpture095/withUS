package com.withus.project.dto.members.parteners;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HistoryDTO {
    private Integer historyIdx; // 경력 ID 기본키 (요청용)
    private String historyId; // UUID(응답용)
    private Integer partnerIdx; // 파트너 ID (요청용)
    private String duration; // 경력 기간 (요청/응답 공용)
    private String joinDate; // 입사일 (요청/응답 공용, yyyy-MM-dd 형식)
    private String exitDate; // 퇴사일 (요청/응답 공용, yyyy-MM-dd 형식)
    private String companyName; // 회사명 (요청/응답 공용)
    private String work;
}
