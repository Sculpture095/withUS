package com.withus.project.domain.boards;

import com.withus.project.domain.members.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "board",
        uniqueConstraints = @UniqueConstraint(name = "uk_board_id", columnNames = "board_id") // UNIQUE KEY 이름 지정
)
@NoArgsConstructor
@AllArgsConstructor
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_idx")
    private Integer boardIdx;

    @Column(name = "board_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    private String boardId ; // UUID 고유 식별자

    @ManyToOne
    @JoinColumn(name = "member_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_board_member"))
    private MemberEntity member; // MemberEntity를 참조하는 외래키

    @Enumerated(EnumType.STRING)
    @Column(name = "ifntype_id", nullable = false)
    private BoardType boardType; // 게시판 유형

    @Column(name = "subject", nullable = false, length = 250)
    private String subject; // 글 제목

    @Column(name = "content", nullable = false, length = 1500)
    private String content; // 글 내용

    @Column(name = "createdate", nullable = false)
    private LocalDate createDate = LocalDate.now(); // 등록일

    @Column(name = "filepath", length = 300)
    private String filePath; // 첨부 파일

    @Column(name = "viewcount", nullable = false)
    private Integer viewCount = 0; // 조회수 (기본값)

    @Column(name = "likecount", nullable = false)
    private Integer likeCount = 0; // 추천수 (기본값)

    @PrePersist
    public void prePersist() {
        if (this.boardId == null){
            this.boardId = UUID.randomUUID().toString();
        }
    }


}
