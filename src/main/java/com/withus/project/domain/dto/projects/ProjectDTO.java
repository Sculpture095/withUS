package com.withus.project.domain.dto.projects;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProjectDTO {
    private Integer projectIdx; // 프로젝트 ID 기본키 (요청용)
    private String projectId; // 고유식별자 (응답용)
    private Integer clientIdx; // 클라이언트 ID (요청용)
    private String memberId; // 회원 ID (요청 및 응답용)
    private String projectName; // 프로젝트 명 (요청 및 응답용)
    private String proStatement; // 프로젝트 상태 (요청 및 응답용)
    private String proStatementDescription; // 🔥 ENUM 한글 값 (ex: 모집중)
    private Double amount; // 프로젝트 금액 (요청 및 응답용)
    private String duration; // 프로젝트 기간 (요청 및 응답용)
    private String startDate; // 프로젝트 시작일자 (요청 및 응답용)
    private String closingDate; // 프로젝트 마감일 (요청 및 응답용)
    private String construction; // 프로젝트 종류 (요청 및 응답용)
    private String constructionDescription; // 🔥 ENUM 한글 값 (ex: 외주)
    private String projectLocation; // 프로젝트 위치 (요청 및 응답용)
    private Integer teamSize; // 프로젝트 모집 인원 (요청 및 응답용)
    private String projectInfo; // 프로젝트 소개 (요청 및 응답용)
    private Boolean isCompleted; // 프로젝트 완료 여부 (요청 및 응답용)
    private String registrationDate; // 프로젝트 마감일 (요청 및 응답용)
    private List<String> skills; // 기술 목록

}
