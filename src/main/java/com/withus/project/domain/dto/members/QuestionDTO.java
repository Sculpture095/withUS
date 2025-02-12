package com.withus.project.domain.dto.members;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class QuestionDTO {
    private Integer questionIdx; // 문의 ID 기본키 (요청용)
    private String questionId; // UUID 기본키 (응답용)
    private Integer memberIdx; // 회원 ID
    private String subject; // 제목
    private String content; // 내용
    private String attachment; // 첨부파일
}
