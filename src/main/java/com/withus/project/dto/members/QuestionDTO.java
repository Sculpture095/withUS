package com.withus.project.dto.members;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class QuestionDTO {
    private Integer questionIdx; // 문의 ID 기본키 (요청용)
    private String questionId; // UUID 기본키 (응답용)
    private Integer memberIdx; // 회원 ID
    private String subject; // 제목
    private String content; // 내용
    private String attachment; // 첨부파일
    private Boolean isAnswered; // 답변 여부

}
