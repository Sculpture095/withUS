package com.withus.project.domain.dto.boards;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Integer boardIdx; // 게시글 ID 기본키 (요청용)
    private String boardId; // UUID 고유식별자 (응답용)
    private Integer memberIdx; // 작성자 ID (요청용)
    private String boardType; // 게시판 구분
    private String subject; // 제목
    private String content; // 내용
    private String filePath; // 첨부파일 경로
    private String createDate; // 작성일 (응답용, String으로 변환)
    private Integer viewCount; // 조회수 (응답용)
    private Integer likeCount; // 추천수 (응답용)
}
