package com.withus.project.domain.projects;

import com.withus.project.config.UUIDToStringConverter;
import com.withus.project.domain.members.ClientEntity;
import com.withus.project.domain.members.PartnerEntity;
import com.withus.project.domain.members.SelectSkillEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "case_table",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_case_id", columnNames = "case_id")
        })
@NoArgsConstructor
@ToString
public class CaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_idx")
    private Integer caseIdx;

    @Column(name = "case_id", columnDefinition = "CHAR(36)", unique = true, nullable = false, updatable = false)
    @Convert(converter = UUIDToStringConverter.class)
    private UUID caseId;

    @ManyToOne
    @JoinColumn(name = "contract_idx", nullable = false, foreignKey = @ForeignKey(name = "fk_case_contract"))
    private ContractEntity contract; // ContractEntity를 참조하는 외래키

    @Column(name = "title", length = 60, nullable = false)
    private String title; // 고객 사례 제목

    @Column(name = "content", length = 200, nullable = false)
    private String content; // 고객 사례 내용

    @Column(name = "createDate", nullable = false)
    private LocalDate createDate; // 작성일자

    @Enumerated(EnumType.STRING)
    @Column(name = "rating", length = 30, nullable = false)
    private Rating rating; // 평가

    @Column(name = "caseimg", length = 500, nullable = false)
    private String caseImg; // 사례 이미지

    @Column(name = "caseamount", nullable = false)
    private Double caseAmount; // 계약 금액

    @Column(name = "timeline", nullable = false)
    private LocalDate timeline; // 프로젝트 일정

    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL)
    private List<SelectSkillEntity> relatedTechs; // 관련 기술 리스트


    @Column(name = "thumbnail", length = 300)
    private String thumbnail; // 썸네일 URL

    @PrePersist
    public void prePersist() {
        if (this.caseId == null){
            this.caseId = UUID.randomUUID();
        }
    }
}
