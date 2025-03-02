package com.withus.project.dto.boards;

import lombok.*;

import java.util.List;

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
    private String createDate; // 🕒 String으로 변환하여 yyyy-MM-dd HH:mm 형식으로 전달

    @Builder.Default
    private Integer viewCount = 0; // 조회수 (기본값)

    @Builder.Default
    private Integer likeCount = 0; // 추천수 (기본값)

    // ✅ 댓글 목록 추가 (응답용)
    private List<RemarkDTO> remarks;
}
