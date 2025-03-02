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
    @JoinColumn(name = "contract_idx", foreignKey = @ForeignKey(name="fk_case_contract_of_case_table"))
    private ContractEntity contract;

    @Column(name = "title", length = 60, nullable = false)
    private String title; // 고객 사례 제목

    @Column(name = "content", length = 500, nullable = true)
    private String content; // 고객 사례 내용

    @Column(name = "shortcontent", length = 200, nullable = true)
    private String shortContent; //짧은 후기

    @Column(name = "createDate", nullable = false)
    private LocalDate createDate = LocalDate.now(); // 작성일자


    @Column(name = "rating", length = 30, nullable = false)
    private Double rating; // 평가

    @Column(name = "caseimg", length = 500, nullable = true)
    private String caseImg; // 사례 이미지


    @OneToMany(mappedBy = "caseEntity", cascade = CascadeType.ALL)
    private List<SelectSkillEntity> relatedTechs; // 관련 기술 리스트


    @PrePersist
    public void prePersist() {
        if (this.caseId == null){
            this.caseId = UUID.randomUUID();
        }
    }
}
