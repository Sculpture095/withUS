package com.withus.project.domain.boards;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.withus.project.domain.members.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "remark",
        uniqueConstraints = @UniqueConstraint(name = "uk_remark_id", columnNames = "remark_id"))
@NoArgsConstructor
@AllArgsConstructor
public class RemarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "remark_idx")
    private Integer remarkIdx;

    @Column(name = "remark_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    private String remarkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_remark_board"))
    @OnDelete(action = OnDeleteAction.CASCADE) // 게시글 삭제 시 댓글 자동 삭제
    @JsonIgnore //순환 참조 방지
    private BoardEntity board; // BoardEntity를 참조하는 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_remark_member"))
    private MemberEntity member; // MemberEntity를 참조하는 외래키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_idx", foreignKey = @ForeignKey(name = "fk_remark_parent"))
    private RemarkEntity parentRemark; // 부모 댓글 참조

    @OneToMany(mappedBy = "parentRemark", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference //자식 댓글 직력화 허용
    private List<RemarkEntity> childRemarks = new ArrayList<>(); // 자식 댓글 목록

    @Column(name = "depth", nullable = false)
    private Integer depth = 0; // 댓글의 계층 깊이

    @Column(name = "remarks", nullable = false, length = 300)
    private String remarks; // 댓글 내용

    @Column(name = "createdate", nullable = false)
    private LocalDateTime createDate = LocalDateTime.now(); // 등록일

    @Column(name = "likecount", nullable = false)
    private Integer likeCount = 0; // 추천수 (기본값)

    @PrePersist
    public void prePersist() {
        if (this.remarkId == null){
            this.remarkId = UUID.randomUUID().toString();
        }
    }

    // 댓글 추천 증가
    public void increaseLikeCount() {
        this.likeCount += 1;
    }


}
