package com.withus.project.domain.members;

import com.withus.project.config.UUIDToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "questions",
        uniqueConstraints = @UniqueConstraint(name = "uk_question_id", columnNames = "question_id"))
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_idx")
    private Integer questionIdx;

    @Column(name = "question_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_idx", nullable = false,foreignKey = @ForeignKey(name = "fk_question_member"))
    private MemberEntity member; // MemberEntity를 참조하는 외래키

    @Column(name = "subject", nullable = false, length = 50)
    private String subject; // 문의 제목

    @Column(name = "content", nullable = false, length = 300)
    private String content; // 문의 내용

    @Column(name = "attachment", length = 300)
    private String attachment; // 첨부 파일

    @Column(name = "is_answered", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isAnswered = false;


    @PrePersist
    public void prePersist() {
        if (this.questionId == null){
            this.questionId = UUID.randomUUID();
        }

        if (this.isAnswered == null) {
            this.isAnswered = false;
        }
    }
}
