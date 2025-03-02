package com.withus.project.dto.boards;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemarkDTO {

    private Integer remarkIdx; // 댓글 ID 기본키 (요청 및 응답용)
    private String boardId; // 게시판 ID (요청 및 응답용)
    private String remarkId;//
    private Integer memberIdx; // 작성자 ID (요청 및 응답용)
    private String remarks; // 댓글 내용 (요청 및 응답용)
    private String createDate; // 작성일 (응답용, String으로 변환)
    private Integer likeCount; // 추천수 (응답용)
    private String parentRemarkId; // 부모 댓글 ID (요청 및 응답용)
}
